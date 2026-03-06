package com.zeroq.back.service.sensor.vo;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterSensorDeviceRequest {
    @NotBlank
    @Size(max = 50)
    private String sensorId;

    @NotBlank
    @Size(max = 20)
    private String macAddress;

    @NotBlank
    @Size(max = 100)
    private String model;

    @Size(max = 20)
    private String firmwareVersion;

    @NotBlank
    private String type;

    private String protocol = "MQTT";

    @Positive
    private Long placeId;

    private String positionCode;

    @DecimalMin(value = "0.1")
    private Double occupancyThresholdCm;

    private Double calibrationOffsetCm;
    private String metadataJson;
}
