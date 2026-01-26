package com.zeroq.back.database.pub.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "space_insights", indexes = {
        @Index(name = "idx_space_id", columnList = "space_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpaceInsights extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    private Space space;

    @Column(nullable = false)
    private int weeklyVisitors; // 주간 방문자 수

    @Column(nullable = false)
    private int monthlyVisitors; // 월간 방문자 수

    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private double growthRate; // 성장률 (%)

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int busiestDayOfWeek; // 가장 붐비는 요일 (1-7)

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int quietestDayOfWeek; // 가장 한산한 요일 (1-7)

    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private double customerSatisfaction; // 고객 만족도 (0-5)

    @Column(nullable = false)
    private int totalReviews; // 총 리뷰 수

    @Column(length = 500)
    private String keyInsights; // 주요 인사이트

    @Column(length = 500)
    private String recommendations; // 개선 권고사항
}
