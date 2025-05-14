package app.esp32_rms.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sensor_data")
@Getter
@Setter

public class SensorData {

    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false) 
    private Device device;

    @Column(nullable = false)
    private Double temperature; // Temperatura registrada

    @Column(nullable = false)
    private Double humidity; // Umidade registrada

    @Column(nullable = false)
    private boolean isConnected = false;

    @Column(nullable = false)
    private Instant timestamp; // Timestamp da leitura
}