package com.zeroq.back.database.pub.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "peak_hours", indexes = {
        @Index(name = "idx_space_id_day", columnList = "space_id,day_of_week")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeakHours extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    private Space space;

    @Column(nullable = false)
    private int dayOfWeek; // 요일 (1=월, 2=화, ..., 7=일)

    @Column(nullable = false)
    private int hourOfDay; // 시간 (0-23)

    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private double averageOccupancy; // 해당 시간의 평균 점유율

    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private double peakOccupancy; // 해당 시간의 최고 점유율

    @Column(nullable = false)
    private int dataCount = 0; // 데이터 수집 수

    @Column(length = 500)
    private String note;
}
