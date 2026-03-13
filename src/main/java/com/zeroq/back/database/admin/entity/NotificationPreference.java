package com.zeroq.back.database.admin.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification_preference")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreference extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id", nullable = false, unique = true)
    private Long profileId;

    @Builder.Default
    @Column(nullable = false)
    private boolean allNotificationsEnabled = true; // 모든 알림 활성화

    @Builder.Default
    @Column(nullable = false)
    private boolean occupancyNotificationsEnabled = true; // 점유율 알림

    @Builder.Default
    @Column(nullable = false)
    private boolean batteryNotificationsEnabled = true; // 배터리 알림

    @Builder.Default
    @Column(nullable = false)
    private boolean reviewNotificationsEnabled = true; // 리뷰 알림

    @Builder.Default
    @Column(nullable = false)
    private boolean promotionNotificationsEnabled = true; // 프로모션 알림

    @Builder.Default
    @Column(nullable = false)
    private boolean emailNotificationsEnabled = false; // 이메일 알림

    @Builder.Default
    @Column(nullable = false)
    private boolean pushNotificationsEnabled = true; // 푸시 알림

    @Builder.Default
    @Column(nullable = false)
    private boolean smsNotificationsEnabled = false; // SMS 알림
}
