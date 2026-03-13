package com.zeroq.back.service.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AdminSensorResponse {
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
    private LocalDateTime lastHeartbeatAt;
    private Long lastSequenceNo;
    private String spaceName;
    private double occupancyRate;
    private String gatewayId;
    private String detectionStatus;
    private String signalStrength;
    private String locationLabel;
    private String batteryLabel;
}
