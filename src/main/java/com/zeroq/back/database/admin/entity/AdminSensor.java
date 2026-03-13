package com.zeroq.back.database.admin.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_registry", indexes = {
        @Index(name = "idx_admin_sensor_gateway_status", columnList = "gateway_id,status"),
        @Index(name = "idx_admin_sensor_sensor_id", columnList = "sensor_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSensor extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sensor_id", nullable = false, unique = true, length = 50)
    private String sensorId;

    @Column(name = "mac_address", nullable = false, unique = true, length = 20)
    private String macAddress;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(name = "firmware_version", length = 20)
    private String firmwareVersion;

    @Column(nullable = false, length = 30)
    private String type;

    @Column(nullable = false, length = 20)
    private String protocol;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "gateway_id", length = 50)
    private String gatewayId;

    @Column(name = "position_code", length = 50)
    private String positionCode;

    @Column(name = "battery_percent")
    private Double batteryPercent;

    @Column(name = "occupancy_threshold_cm")
    private Double occupancyThresholdCm;

    @Column(name = "calibration_offset_cm")
    private Double calibrationOffsetCm;

    @Column(name = "last_heartbeat_at")
    private LocalDateTime lastHeartbeatAt;

    @Column(name = "last_sequence_no")
    private Long lastSequenceNo;

    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;
}
