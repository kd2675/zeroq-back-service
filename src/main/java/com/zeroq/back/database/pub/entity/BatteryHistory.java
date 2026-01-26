package com.zeroq.back.database.pub.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "battery_history", indexes = {
        @Index(name = "idx_sensor_id_created_at", columnList = "sensor_id,created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatteryHistory extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private double percentage; // 배터리 %

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BatteryStatus.BatteryStatusLevel level; // 배터리 레벨

    @Column(nullable = false)
    private int estimatedDaysRemaining; // 예상 남은 일수

    @Column(length = 500)
    private String note;
}
