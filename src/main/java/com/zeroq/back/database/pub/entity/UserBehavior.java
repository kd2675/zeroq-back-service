package com.zeroq.back.database.pub.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_behavior", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBehavior extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private int totalVisits; // 방문 횟수

    @Column(nullable = false)
    private int favoritesCount; // 즐겨찾기 수

    @Column(nullable = false)
    private int reviewsCount; // 리뷰 수

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int preferredTimeHour; // 선호 시간대 (0-23)

    @Column(nullable = false)
    private String preferredCategoryId; // 선호 카테고리

    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private double averageOccupancyPreference; // 선호 점유율

    @Column(length = 500)
    private String note;
}
