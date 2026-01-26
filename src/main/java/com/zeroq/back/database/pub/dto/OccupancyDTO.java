package com.zeroq.back.database.pub.dto;

import com.zeroq.back.database.pub.entity.CrowdLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OccupancyDTO {
    private Long spaceId;
    private String spaceName;
    private int currentOccupancy;
    private int maxCapacity;
    private double occupancyPercentage;
    private CrowdLevel crowdLevel;
    private LocalDateTime lastUpdated;
}
