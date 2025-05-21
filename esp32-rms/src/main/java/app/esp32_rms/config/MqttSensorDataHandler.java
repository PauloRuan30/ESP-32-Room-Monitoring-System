package app.esp32_rms.config;

import app.esp32_rms.model.Device;
import app.esp32_rms.model.SensorData;
import app.esp32_rms.service.DeviceService;
import app.esp32_rms.service.SensorDataService;
import app.esp32_rms.service.WebSocketService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;

import java.time.Instant;
// import java.util.UUID;

@Slf4j
@Component
public class MqttSensorDataHandler implements MessageHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private SensorDataService sensorDataService;

    @Autowired
    private DeviceService deviceService; // ⬅️ Precisamos acessar o Device para associar


    @Override
    public void handleMessage(@NonNull Message<?> message) throws MessagingException {
        try {
            // Obter o payload como string
            String payload = message.getPayload().toString();
            
            // Obter o tópico da mensagem
            Object topicHeader = message.getHeaders().get("mqtt_receivedTopic");
            if (topicHeader == null) {
                log.warn("Tópico MQTT ausente no cabeçalho da mensagem.");
                return;
            }
            String topic = topicHeader.toString();
            
            log.debug("Mensagem MQTT recebida de [{}]: {}", topic, payload);
            
            // Extrair o ID do dispositivo do tópico (assumindo formato first_spring_app/sensor/deviceId)
            // String deviceIdString = topic.split("/")[2];
            // Converter a String para UUID
            // UUID deviceId = UUID.fromString(deviceIdString);

            // Device device = new Device();
            // device.setName("esp32");
            // device.setType("temperatura e humidade");
            // device.setLocation("Sala - 10");

            // Buscar o Device no banco de dados
            // Device device = deviceService.get(deviceId);
            
            Device device = deviceService.getByName("esp32");
            if (device == null) {
                log.warn("Device 'esp32' não encontrado no banco.");
                return;
            }
            
            // Parse do JSON
            JsonNode jsonNode = objectMapper.readTree(payload);

            // Criar nova instância de SensorData
            SensorData sensorData = new SensorData();
            sensorData.setDevice(device); // associar corretamente
            
            // Criar objeto SensorData
            // sensorData.setId(deviceId);
            
            // Extrair dados do JSON
            if (jsonNode.has("temperature")) {
                sensorData.setTemperature(jsonNode.get("temperature").doubleValue());
            }
            
            if (jsonNode.has("humidity")) {
                sensorData.setHumidity(jsonNode.get("humidity").doubleValue());
            }
            
            // Timestamp (usar o do dispositivo ou gerar um novo)
            if (jsonNode.has("timestamp")) {
                sensorData.setTimestamp(Instant.ofEpochSecond(jsonNode.get("timestamp").longValue()));
            } else {
                sensorData.setTimestamp(Instant.now());
            }
            
            // Status do dispositivo
            sensorData.setConnected(true);
            
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