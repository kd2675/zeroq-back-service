package com.zeroq.back.service.sensor.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSensorCommandRequest {
    @NotBlank
    private String sensorId;

    @NotBlank
    private String commandType;

    private String commandPayload;
}
