package com.zeroq.back.database.pub.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "low_battery_alert", indexes = {
        @Index(name = "idx_sensor_id", columnList = "sensor_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LowBatteryAlert extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private double batteryPercentage; // 알림 발생 시 배터리 %

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BatteryStatus.BatteryStatusLevel level; // 배터리 레벨

    @Column(nullable = false)
    private boolean acknowledged = false; // 확인 여부

    @Column
    private LocalDateTime acknowledgedAt; // 확인 시간

    @Column(length = 500)
    private String note;
}
