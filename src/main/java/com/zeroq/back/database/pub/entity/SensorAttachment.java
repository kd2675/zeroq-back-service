package com.zeroq.back.database.pub.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_attachment", indexes = {
        @Index(name = "idx_sensor_id_space_id", columnList = "sensor_id,space_id"),
        @Index(name = "idx_attached_at", columnList = "attached_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorAttachment extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    private Space space;

    @Column(nullable = false)
    private LocalDateTime attachedAt; // 부착 시간

    @Column
    private LocalDateTime detachedAt; // 분리 시간 (null이면 현재 부착 중)

    @Column(length = 100)
    private String attachmentLocation; // 부착 위치 상세 (예: 천장, 벽 등)

    @Column(nullable = false)
    private boolean active = true; // 활성 여부

    @Column(length = 500)
    private String note;

    /**
     * 센서가 현재 부착 중인지 확인
     */
    public boolean isCurrentlyAttached() {
        return detachedAt == null && active;
    }
}
