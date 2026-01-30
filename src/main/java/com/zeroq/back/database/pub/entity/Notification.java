package com.zeroq.back.database.pub.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification", indexes = {
        @Index(name = "idx_user_id_created_at", columnList = "user_id,created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String title; // 제목

    @Column(nullable = false, length = 500)
    private String message; // 메시지

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType type; // 알림 타입

    @Column(name = "read_yn", nullable = false)
    private boolean read = false; // 읽음 여부

    @Column(length = 500)
    private String relatedEntityId; // 관련 엔티티 ID

    @Column(length = 100)
    private String relatedEntityType; // 관련 엔티티 타입

    /**
     * 알림 타입
     */
    public enum NotificationType {
        SPACE_OCCUPANCY("공간 점유율"),
        LOW_BATTERY("저전량"),
        NEW_REVIEW("새 리뷰"),
        FAVORITE_SPACE("즐겨찾기 공간"),
        SYSTEM("시스템"),
        PROMOTION("프로모션");

        private final String description;

        NotificationType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
