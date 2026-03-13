package com.zeroq.back.service.gateway.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayRegistryResponse {
    private String gatewayId;
    private String gatewayName;
    private String gatewayRole;
    private String regionCode;
    private Long spaceId;
    private String spaceName;
    private String locationLabel;
    private String ipAddress;
    private Integer sensorCapacity;
    private String firmwareVersion;
    private String description;
    private String linkedBridge;
    private String status;
}
