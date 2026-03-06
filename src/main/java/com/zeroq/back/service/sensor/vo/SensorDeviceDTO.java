package com.zeroq.back.service.sensor.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SensorDeviceDTO {
    private Long id;
    private String sensorId;
    private String macAddress;
    private String model;
    private String firmwareVersion;
    private String type;
    private String protocol;
    private String status;
    private Long placeId;
    private String positionCode;
    private Double batteryPercent;
    private Double occupancyThresholdCm;
    private Double calibrationOffsetCm;
    private LocalDateTime lastHeartbeatAt;
    private Long lastSequenceNo;
    private String metadataJson;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}
