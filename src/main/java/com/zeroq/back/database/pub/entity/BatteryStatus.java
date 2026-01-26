package com.zeroq.back.database.pub.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "battery_status", indexes = {
        @Index(name = "idx_sensor_id", columnList = "sensor_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatteryStatus extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private double currentPercentage; // 현재 배터리 %

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int estimatedDaysRemaining; // 예상 남은 일수

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BatteryStatusLevel level; // 배터리 레벨

    @Column(nullable = false)
    private long lastMeasuredTimestamp; // 마지막 측정 시간

    @Column(nullable = false)
    private boolean alertSent = false; // 저전량 알림 발송 여부

    /**
     * 배터리 상태 레벨
     */
    public enum BatteryStatusLevel {
        FULL("충분", 75, 100),
        GOOD("양호", 50, 74),
        LOW("부족", 20, 49),
        CRITICAL("위험", 0, 19);

        private final String name;
        private final int minPercentage;
        private final int maxPercentage;

        BatteryStatusLevel(String name, int minPercentage, int maxPercentage) {
            this.name = name;
            this.minPercentage = minPercentage;
            this.maxPercentage = maxPercentage;
        }

        public static BatteryStatusLevel fromPercentage(double percentage) {
            if (percentage >= 75) return FULL;
            if (percentage >= 50) return GOOD;
            if (percentage >= 20) return LOW;
            return CRITICAL;
        }

        public String getName() {
            return name;
        }
    }
}
