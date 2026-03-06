package com.zeroq.back.database.pub.dto;
import com.zeroq.back.common.jpa.CommonDateDTO;
import com.zeroq.back.database.pub.entity.UserLocation;
import lombok.*;

import java.time.LocalDateTime;

/**
 * UserLocation DTO
 * - 사용자 위치 정보 전송 객체
 * - 인증 사용자의 userKey 기반 위치 정보 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLocationDTO extends CommonDateDTO {
    private Long id;
    private String userKey;
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
                .userKey(entity.getUserKey())
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

}
