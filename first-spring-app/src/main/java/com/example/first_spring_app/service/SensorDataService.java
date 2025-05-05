package com.example.first_spring_app.service;

import com.example.first_spring_app.model.Device;
import com.example.first_spring_app.repository.SensorDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class SensorDataService {

    @Autowired
    private SensorDataRepository sensorDataRepository;

    public Device save(Device device) {
        return sensorDataRepository.save(device);
    }

    public List<Device> findAllByDeviceId(String deviceId) {
        return sensorDataRepository.findByDeviceIdOrderByTimestampDesc(deviceId);
    }

    public Optional<Device> findLatestByDeviceId(String deviceId) {
        return sensorDataRepository.findTopByDeviceIdOrderByTimestampDesc(deviceId);
    }

    public List<Device> findByTimeRange(Instant start, Instant end) {
        return sensorDataRepository.findByTimestampBetweenOrderByTimestampAsc(start, end);
    }

    public Map<String, Object> getDeviceStatistics(String deviceId) {
        Map<String, Object> stats = new HashMap<>();
        
        // Obter dados do último dia
        Instant now = Instant.now();
        Instant oneDayAgo = now.minus(1, ChronoUnit.DAYS);
        
        // Média de temperatura e umidade
        Float avgTemp = sensorDataRepository.findAverageTemperatureByDeviceAndTimeRange(
                deviceId, oneDayAgo, now);
        Float avgHumidity = sensorDataRepository.findAverageHumidityByDeviceAndTimeRange(
                deviceId, oneDayAgo, now);
        
        // Último status do dispositivo
        Optional<Device> latestData = findLatestByDeviceId(deviceId);
        
        stats.put("deviceId", deviceId);
        stats.put("avgTemperature", avgTemp);
        stats.put("avgHumidity", avgHumidity);
        stats.put("lastReading", latestData.orElse(null));
        stats.put("isConnected", latestData.map((Device device) -> {
            // Considerar dispositivo conectado se último dado for mais recente que 5 minutos
            Instant fiveMinutesAgo = now.minus(5, ChronoUnit.MINUTES);
            return device.getTimestamp().isAfter(fiveMinutesAgo);
        }).orElse(false));
        
        return stats;
    }
}