package com.zeroq.back.database.pub.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "occupancy_history", indexes = {
        @Index(name = "idx_space_id_created_at", columnList = "space_id,created_at"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OccupancyHistory extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    private Space space;

    @Column(nullable = false)
    private int occupancyCount; // 점유 인원

    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private double occupancyPercentage; // 점유율

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CrowdLevel crowdLevel; // 혼잡도 레벨

    @Column(nullable = false)
    private int maxCapacity; // 해당 시점의 최대 수용인원

    @Column(length = 500)
    private String note; // 메모
}
