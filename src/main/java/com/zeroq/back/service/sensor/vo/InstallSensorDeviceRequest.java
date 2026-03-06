package com.zeroq.back.service.sensor.vo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstallSensorDeviceRequest {
    @NotNull
    @Positive
    private Long placeId;

    private String positionCode;
    private Double occupancyThresholdCm;
    private Double calibrationOffsetCm;
}
