package com.zeroq.back.database.pub.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "analytics_data", indexes = {
        @Index(name = "idx_space_id_created_at", columnList = "space_id,created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsData extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    private Space space;

    @Column(nullable = false)
    private int totalVisitors; // 전체 방문자 수

    @Column(nullable = false)
    private int uniqueVisitors; // 고유 방문자 수

    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private double averageStayTime; // 평균 체류 시간 (분)

    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private double averageOccupancy; // 평균 점유율

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int peakHour; // 피크 시간 (0-23)

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int peakDay; // 피크 요일 (1-7)

    @Column(nullable = false)
    private String period; // 기간 (daily, weekly, monthly)

    @Column(length = 500)
    private String note;
}
