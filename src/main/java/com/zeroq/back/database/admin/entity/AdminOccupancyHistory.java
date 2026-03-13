package com.zeroq.back.database.admin.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import com.zeroq.back.common.model.CrowdLevel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "occupancy_history", indexes = {
        @Index(name = "idx_admin_occ_history_space_created", columnList = "space_id,create_date"),
        @Index(name = "idx_admin_occ_history_created", columnList = "create_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminOccupancyHistory extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    private AdminSpace space;

    @Column(nullable = false)
    private int occupancyCount;

    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private double occupancyPercentage;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CrowdLevel crowdLevel;

    @Column(nullable = false)
    private int maxCapacity;

    @Column(length = 500)
    private String note;
}
