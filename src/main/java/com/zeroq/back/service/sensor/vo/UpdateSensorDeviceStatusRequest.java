package com.zeroq.back.service.sensor.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSensorDeviceStatusRequest {
    @NotBlank
    private String status;
}
