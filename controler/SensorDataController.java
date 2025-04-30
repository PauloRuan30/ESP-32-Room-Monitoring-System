package com.example.smda.controller;

import com.example.smda.model.SensorData;
import com.example.smda.service.SensorDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/sensor")
public class SensorDataController {

    @Autowired
    private SensorDataService sensorDataService;

    @GetMapping("/{deviceId}/latest")
    public ResponseEntity<SensorData> getLatestData(@PathVariable String deviceId) {
        Optional<SensorData> latestData = sensorDataService.findLatestByDeviceId(deviceId);
        return latestData.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{deviceId}/history")
    public ResponseEntity<List<SensorData>> getHistoricalData(
            @PathVariable String deviceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        Instant start = startTime != null 
                ? startTime.atZone(ZoneId.systemDefault()).toInstant() 
                : Instant.now().minus(java.time.Duration.ofDays(1));
        
        Instant end = endTime != null 
                ? endTime.atZone(ZoneId.systemDefault()).toInstant() 
                : Instant.now();
        
        List<SensorData> data = sensorDataService.findByTimeRange(start, end);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/{deviceId}/stats")
    public ResponseEntity<Map<String, Object>> getDeviceStatistics(@PathVariable String deviceId) {
        Map<String, Object> stats = sensorDataService.getDeviceStatistics(deviceId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/devices")
    public ResponseEntity<List<String>> getDeviceList() {
        // Implementação simplificada para MVP - no futuro, isso viria de um registro de dispositivos
        return ResponseEntity.ok(List.of("ambiente1", "ambiente2", "ambiente3"));
    }
}