package com.zeroq.back.service.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminGatewayResponse {
    private String gatewayId;
    private String gatewayName;
    private String gatewayRole;
    private String regionCode;
    private Long spaceId;
    private String spaceName;
    private String locationLabel;
    private String ipAddress;
    private Integer sensorCapacity;
    private Integer currentSensorLoad;
    private String firmwareVersion;
    private String signalStrength;
    private Integer throughputMbps;
    private String status;
    private String linkedBridge;
    private Integer latencyMs;
    private Double packetLossPercent;
    private List<AdminSensorResponse> connectedSensors;
    private LocalDateTime lastHeartbeatAt;
}
