package com.example.smda.config;

import com.example.smda.model.SensorData;
import com.example.smda.service.SensorDataService;
import com.example.smda.service.WebSocketService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
public class MqttSensorDataHandler implements MessageHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    private SensorDataService sensorDataService;
    
    @Autowired
    private WebSocketService webSocketService;

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        try {
            // Obter o payload como string
            String payload = message.getPayload().toString();
            // Obter o tópico da mensagem
            String topic = message.getHeaders().get("mqtt_receivedTopic").toString();
            
            log.debug("Mensagem MQTT recebida de [{}]: {}", topic, payload);
            
            // Extrair o ID do dispositivo do tópico (assumindo formato smda/sensor/deviceId)
            String deviceId = topic.split("/")[2];
            
            // Parse do JSON
            JsonNode jsonNode = objectMapper.readTree(payload);
            
            // Criar objeto SensorData
            SensorData sensorData = new SensorData();
            sensorData.setDeviceId(deviceId);
            
            // Extrair dados do JSON
            if (jsonNode.has("temperature")) {
                sensorData.setTemperature(jsonNode.get("temperature").floatValue());
            }
            
            if (jsonNode.has("humidity")) {
                sensorData.setHumidity(jsonNode.get("humidity").floatValue());
            }
            
            // Timestamp (usar o do dispositivo ou gerar um novo)
            if (jsonNode.has("timestamp")) {
                sensorData.setTimestamp(Instant.ofEpochSecond(jsonNode.get("timestamp").longValue()));
            } else {
                sensorData.setTimestamp(Instant.now());
            }
            
            // Status do dispositivo
            sensorData.setIsConnected(true);
            
            // Salvar os dados
            sensorDataService.save(sensorData);
            
            // Enviar dados para clientes WebSocket
            webSocketService.sendSensorUpdate(sensorData);
            
            log.debug("Dados do sensor salvos com sucesso: {}", sensorData);
        } catch (Exception e) {
            log.error("Erro ao processar mensagem MQTT", e);
        }
    }
}