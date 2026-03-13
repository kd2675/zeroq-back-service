package com.zeroq.back.service.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminLogResponse {
    private String id;
    private String timestamp;
    private String eventType;
    private String targetLabel;
    private String severity;
    private String details;
    private Long spaceId;
    private String sensorId;
    private String gatewayId;
}
