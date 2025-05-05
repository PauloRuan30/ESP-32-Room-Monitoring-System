package com.example.first_spring_app.config;

// import com.example.first_spring_app.model.SensorData;
import com.example.first_spring_app.model.Device;
import com.example.first_spring_app.service.SensorDataService;
import com.example.first_spring_app.service.WebSocketService;
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
    private Device device;
    
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
            
            // Extrair o ID do dispositivo do tópico (assumindo formato first_spring_app/sensor/deviceId)
            String deviceId = topic.split("/")[2];
            
            // Parse do JSON
            JsonNode jsonNode = objectMapper.readTree(payload);
            
            // Criar objeto SensorData
            device.setId(deviceId);
            
            // Extrair dados do JSON
            if (jsonNode.has("temperature")) {
                device.setTemperature(jsonNode.get("temperature").floatValue());
            }
            
            if (jsonNode.has("humidity")) {
                device.setHumidity(jsonNode.get("humidity").floatValue());
            }
            
            // Timestamp (usar o do dispositivo ou gerar um novo)
            if (jsonNode.has("timestamp")) {
                device.setTimestamp(Instant.ofEpochSecond(jsonNode.get("timestamp").longValue()));
            } else {
                device.setTimestamp(Instant.now());
            }
            
            // Status do dispositivo
            device.setIsConnected(true);
            
            // Salvar os dados
            sensorDataService.save(device);
            
            // Enviar dados para clientes WebSocket
            webSocketService.sendSensorUpdate(device);
            
            log.debug("Dados do sensor salvos com sucesso: {}", device);
        } catch (Exception e) {
            log.error("Erro ao processar mensagem MQTT", e);
        }
    }
}