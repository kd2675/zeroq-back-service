package com.zeroq.back.database.pub.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_location", indexes = {
        @Index(name = "idx_profile_id_space_id", columnList = "profile_id,space_id"),
        @Index(name = "idx_visited_at", columnList = "visited_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLocation extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "space_id", nullable = false)
    private Long spaceId;

    @Column(nullable = false)
    private LocalDateTime visitedAt; // 방문 시간

    @Column
    private LocalDateTime leftAt; // 떠난 시간

    @Column
    private int durationMinutes; // 체류 시간 (분)

    @Column(nullable = false)
    private double latitude; // 위도

    @Column(nullable = false)
    private double longitude; // 경도

    @Column(length = 500)
    private String note;
}
