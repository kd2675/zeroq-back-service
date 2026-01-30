package com.zeroq.back.database.pub.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Create UserLocation Request
 * - 사용자 위치 생성 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserLocationRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Space ID is required")
    private Long spaceId;

    @NotNull(message = "Visited time is required")
    private LocalDateTime visitedAt;

    private LocalDateTime leftAt;

    private Integer durationMinutes;

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    private String note;
}
