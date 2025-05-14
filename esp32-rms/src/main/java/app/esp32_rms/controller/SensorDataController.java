package app.esp32_rms.controller;

import app.esp32_rms.model.SensorData;
import app.esp32_rms.service.DeviceService;
import app.esp32_rms.service.SensorDataService;
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
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sensor")
public class SensorDataController {

    @Autowired
    private SensorDataService sensorDataService;
    private DeviceService deviceService;

    @GetMapping("/{deviceId}/latest")
    public ResponseEntity<SensorData> getLatestData(@PathVariable UUID deviceId) {
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
    public ResponseEntity<Map<String, Object>> getDeviceStatistics(@PathVariable UUID deviceId) {
        Map<String, Object> stats = sensorDataService.getDeviceStatistics(deviceId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/devices")
        public ResponseEntity<List<String>> getDeviceList() {
        List<String> deviceIds = deviceService.listAll().stream()
        .map(device -> device.getId().toString())
        .collect(Collectors.toList());
    return ResponseEntity.ok(deviceIds);
}
}