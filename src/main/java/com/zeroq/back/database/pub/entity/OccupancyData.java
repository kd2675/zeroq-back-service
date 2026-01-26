package com.zeroq.back.database.pub.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "occupancy_data", indexes = {
        @Index(name = "idx_space_id", columnList = "space_id"),
        @Index(name = "idx_updated_at", columnList = "updated_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OccupancyData extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    private Space space;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int currentOccupancy; // 현재 점유 인원

    @Column(nullable = false, columnDefinition = "INT DEFAULT 100")
    private int maxCapacity; // 최대 수용인원

    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private double occupancyPercentage; // 점유율 (%)

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CrowdLevel crowdLevel; // 혼잡도 레벨

    @Column(nullable = false)
    private int sensorCount = 0; // 센서 개수

    @Column(nullable = false)
    private long lastUpdatedTimestamp; // 마지막 업데이트 시간 (밀리초)

    /**
     * 점유율 업데이트
     */
    public void updateOccupancy(int currentOccupancy, int maxCapacity) {
        this.currentOccupancy = currentOccupancy;
        this.maxCapacity = maxCapacity;
        this.occupancyPercentage = maxCapacity > 0 ? (currentOccupancy * 100.0 / maxCapacity) : 0;
        this.crowdLevel = CrowdLevel.fromOccupancyPercentage((int) this.occupancyPercentage);
        this.lastUpdatedTimestamp = System.currentTimeMillis();
    }
}
