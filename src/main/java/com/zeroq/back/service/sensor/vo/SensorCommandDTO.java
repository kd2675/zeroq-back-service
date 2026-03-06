package com.zeroq.back.service.sensor.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SensorCommandDTO {
    private Long id;
    private String sensorId;
    private String commandType;
    private String status;
    private String commandPayload;
    private String requestedBy;
    private LocalDateTime requestedAt;
    private LocalDateTime sentAt;
    private LocalDateTime acknowledgedAt;
    private String failureReason;
    private String ackPayload;
}
