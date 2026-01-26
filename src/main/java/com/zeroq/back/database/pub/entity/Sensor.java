package com.zeroq.back.database.pub.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sensor", indexes = {
        @Index(name = "idx_sensor_id", columnList = "sensor_id"),
        @Index(name = "idx_mac_address", columnList = "mac_address")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sensor extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String sensorId; // 물리 센서 ID (UUID 또는 고유 ID)

    @Column(nullable = false, unique = true, length = 20)
    private String macAddress; // MAC 주소

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SensorType type; // 센서 타입

    @Column(nullable = false)
    private String model; // 센서 모델명

    @Column(length = 500)
    private String description; // 설명

    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private double batteryPercentage; // 배터리 percentage (0-100)

    @Column(length = 20)
    private String firmwareVersion; // 펌웨어 버전

    @Column(nullable = false)
    private boolean active = true; // 활성화 여부

    @Column(nullable = false)
    private boolean verified = false; // 검증 여부

    @Column(length = 100)
    private String location; // 위치 정보 (현재 부착 위치)

    @Column(nullable = false)
    private long lastHeartbeat; // 마지막 하트비트 (밀리초)

    @Column(nullable = false)
    private int dataCount = 0; // 수집된 데이터 수
}
