package app.esp32_rms.repository;

import app.esp32_rms.model.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, UUID> {

    List<SensorData> findByDeviceIdOrderByTimestampDesc(UUID deviceId);
    
    Optional<SensorData> findTopByDeviceIdOrderByTimestampDesc(UUID deviceId);
    
    List<SensorData> findByTimestampBetweenOrderByTimestampAsc(Instant start, Instant end);
    
    @Query("SELECT AVG(s.temperature) FROM SensorData s WHERE s.device.id = ?1 AND s.timestamp BETWEEN ?2 AND ?3")
    Float findAverageTemperatureByDeviceAndTimeRange(UUID deviceId, Instant start, Instant end);

    @Query("SELECT AVG(s.humidity) FROM SensorData s WHERE s.device.id = ?1 AND s.timestamp BETWEEN ?2 AND ?3")
    Float findAverageHumidityByDeviceAndTimeRange(UUID deviceId, Instant start, Instant end);

}