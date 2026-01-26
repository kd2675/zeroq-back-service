package com.zeroq.back.database.pub.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_preference")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreference extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String preferenceKey; // 선호도 키

    @Column(nullable = false, length = 500)
    private String preferenceValue; // 선호도 값

    @Column(length = 500)
    private String description;

    // 자주 사용되는 선호도 키들
    public static class Keys {
        public static final String NOTIFICATION_ENABLED = "notification_enabled";
        public static final String DISTANCE_UNIT = "distance_unit"; // km, mile
        public static final String TEMPERATURE_UNIT = "temperature_unit"; // celsius, fahrenheit
        public static final String THEME = "theme"; // light, dark
        public static final String LANGUAGE = "language"; // ko, en
        public static final String LOCATION_TRACKING = "location_tracking";
    }
}
