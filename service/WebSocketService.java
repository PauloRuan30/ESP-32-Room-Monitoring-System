package com.example.smda.service;

import com.example.smda.model.SensorData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendSensorUpdate(SensorData sensorData) {
        String topic = "/topic/sensor/" + sensorData.getDeviceId();
        log.debug("Enviando atualização para WebSocket: {}", topic);
        messagingTemplate.convertAndSend(topic, sensorData);
        
        // Enviar para um tópico geral para atualizações de todos os sensores
        messagingTemplate.convertAndSend("/topic/sensors", sensorData);
    }