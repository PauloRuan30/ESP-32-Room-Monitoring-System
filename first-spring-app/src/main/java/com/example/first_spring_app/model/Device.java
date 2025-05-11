package com.example.first_spring_app.model;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "devices")
@Getter
@Setter

public class Device {

    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Id
    @UuidGenerator
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String location;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}