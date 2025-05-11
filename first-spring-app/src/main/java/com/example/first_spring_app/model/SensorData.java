package com.example.first_spring_app.model;

import java.time.Instant;
import org.hibernate.annotations.UuidGenerator;
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
    @UuidGenerator
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(nullable = false)
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