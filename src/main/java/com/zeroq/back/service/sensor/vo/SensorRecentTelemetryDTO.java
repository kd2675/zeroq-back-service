package com.zeroq.back.service.sensor.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SensorRecentTelemetryDTO {
    private Long telemetryId;
    private String sensorId;
    private Long placeId;
    private Double distanceCm;
    private Boolean occupied;
    private Integer padLeftValue;
    private Integer padRightValue;
    private String qualityStatus;
    private LocalDateTime measuredAt;
    private LocalDateTime receivedAt;
    private Double batteryPercent;
    private Double confidence;
}
