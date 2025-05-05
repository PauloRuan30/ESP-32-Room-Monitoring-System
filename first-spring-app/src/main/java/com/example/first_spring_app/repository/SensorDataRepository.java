package com.example.first_spring_app.repository;

import com.example.first_spring_app.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface SensorDataRepository extends JpaRepository<Device, Long> {

    List<Device> findByDeviceIdOrderByTimestampDesc(String deviceId);
    
    Optional<Device> findTopByDeviceIdOrderByTimestampDesc(String deviceId);
    
    List<Device> findByTimestampBetweenOrderByTimestampAsc(Instant start, Instant end);
    
    @Query("SELECT AVG(s.temperature) FROM device s WHERE s.deviceId = ?1 AND s.timestamp BETWEEN ?2 AND ?3")
    Float findAverageTemperatureByDeviceAndTimeRange(String deviceId, Instant start, Instant end);
    
    @Query("SELECT AVG(s.humidity) FROM device s WHERE s.deviceId = ?1 AND s.timestamp BETWEEN ?2 AND ?3")
    Float findAverageHumidityByDeviceAndTimeRange(String deviceId, Instant start, Instant end);
}