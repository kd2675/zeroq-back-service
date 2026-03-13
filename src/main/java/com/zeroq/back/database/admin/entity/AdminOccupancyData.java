package com.zeroq.back.database.admin.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import com.zeroq.back.common.model.CrowdLevel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "occupancy_data", indexes = {
        @Index(name = "idx_admin_occupancy_space_id", columnList = "space_id"),
        @Index(name = "idx_admin_occupancy_update_date", columnList = "update_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminOccupancyData extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    private AdminSpace space;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int currentOccupancy;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 100")
    private int maxCapacity;

    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private double occupancyPercentage;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CrowdLevel crowdLevel;

    @Column(nullable = false)
    @Builder.Default
    private int sensorCount = 0;

    @Column(nullable = false)
    private long lastUpdatedTimestamp;

    public void updateOccupancy(int currentOccupancy, int maxCapacity) {
        this.currentOccupancy = currentOccupancy;
        this.maxCapacity = maxCapacity;
        this.occupancyPercentage = maxCapacity > 0 ? (currentOccupancy * 100.0 / maxCapacity) : 0.0;
        this.crowdLevel = CrowdLevel.fromOccupancyPercentage((int) this.occupancyPercentage);
        this.lastUpdatedTimestamp = System.currentTimeMillis();
    }
}
