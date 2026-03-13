package com.zeroq.back.database.admin.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gateway_registry", indexes = {
        @Index(name = "idx_admin_gateway_space_id", columnList = "space_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminGateway extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gateway_id", nullable = false, unique = true, length = 50)
    private String gatewayId;

    @Column(name = "space_id")
    private Long spaceId;

    @Column(name = "gateway_name", nullable = false, length = 100)
    private String gatewayName;

    @Column(name = "gateway_role", nullable = false, length = 20)
    @Builder.Default
    private String gatewayRole = "EDGE";

    @Column(name = "region_code", length = 50)
    private String regionCode;

    @Column(name = "location_label", length = 255)
    private String locationLabel;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "sensor_capacity", nullable = false)
    @Builder.Default
    private Integer sensorCapacity = 0;

    @Column(name = "current_sensor_load", nullable = false)
    @Builder.Default
    private Integer currentSensorLoad = 0;

    @Column(name = "latency_ms")
    private Integer latencyMs;

    @Column(name = "packet_loss_percent")
    private Double packetLossPercent;

    @Column(name = "last_heartbeat_at")
    private java.time.LocalDateTime lastHeartbeatAt;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "firmware_version", length = 20)
    private String firmwareVersion;

    @Column(length = 255)
    private String description;

    @Column(name = "linked_bridge", length = 100)
    private String linkedBridge;
}
