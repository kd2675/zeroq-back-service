package com.zeroq.back.service.gateway.vo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateGatewayRequest {
    @NotBlank(message = "gatewayId는 필수입니다")
    private String gatewayId;

    @NotBlank(message = "gatewayName은 필수입니다")
    private String gatewayName;

    @NotNull(message = "spaceId는 필수입니다")
    private Long spaceId;

    private String gatewayRole;

    private String regionCode;

    private String locationLabel;

    private String ipAddress;

    @NotNull(message = "sensorCapacity는 필수입니다")
    @Min(value = 1, message = "sensorCapacity는 1 이상이어야 합니다")
    private Integer sensorCapacity;

    private String firmwareVersion;

    private String description;

    private String linkedBridge;
}
