package com.example.first_spring_app.repository;

import com.example.first_spring_app.model.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {

    List<SensorData> findByDeviceIdOrderByTimestampDesc(String deviceId);
    
    Optional<SensorData> findTopByDeviceIdOrderByTimestampDesc(String deviceId);
    
    List<SensorData> findByTimestampBetweenOrderByTimestampAsc(Instant start, Instant end);
    
    @Query("SELECT AVG(s.temperature) FROM sensor_data s WHERE s.deviceId = ?1 AND s.timestamp BETWEEN ?2 AND ?3")
    Float findAverageTemperatureByDeviceAndTimeRange(String deviceId, Instant start, Instant end);
    
    @Query("SELECT AVG(s.humidity) FROM sensor_data s WHERE s.deviceId = ?1 AND s.timestamp BETWEEN ?2 AND ?3")
    Float findAverageHumidityByDeviceAndTimeRange(String deviceId, Instant start, Instant end);
}