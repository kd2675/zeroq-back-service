package com.zeroq.back.database.pub.dto;

import auth.common.core.dto.UserDto;
import com.zeroq.back.common.jpa.CommonDateDTO;
import com.zeroq.back.database.pub.entity.UserLocation;
import lombok.*;

import java.time.LocalDateTime;

/**
 * UserLocation DTO
 * - 사용자 위치 정보 전송 객체
 * - UserServiceClient를 통해 사용자 정보를 포함
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLocationDTO extends CommonDateDTO {
    private Long id;
    private Long userId;
    private UserDto user; // auth-back-server에서 조회한 사용자 정보
    private Long spaceId;
    private String spaceName;
    private LocalDateTime visitedAt;
    private LocalDateTime leftAt;
    private int durationMinutes;
    private double latitude;
    private double longitude;
    private String note;

    /**
     * Entity to DTO conversion (without user info)
     */
    public static UserLocationDTO from(UserLocation entity) {
        return UserLocationDTO.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .spaceId(entity.getSpace().getId())
                .spaceName(entity.getSpace().getName())
                .visitedAt(entity.getVisitedAt())
                .leftAt(entity.getLeftAt())
                .durationMinutes(entity.getDurationMinutes())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .note(entity.getNote())
                .build();
    }

    /**
     * Entity to DTO conversion (with user info)
     */
    public static UserLocationDTO from(UserLocation entity, UserDto user) {
        UserLocationDTO dto = from(entity);
        dto.setUser(user);
        return dto;
    }
}
