package com.zeroq.back.service.sensor.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SensorPlaceSnapshotDTO {
    private Long placeId;
    private Integer occupiedCount;
    private Integer activeSensorCount;
    private Double occupancyRate;
    private String crowdLevel;
    private LocalDateTime lastMeasuredAt;
    private LocalDateTime lastCalculatedAt;
    private Integer sourceWindowSeconds;
}
