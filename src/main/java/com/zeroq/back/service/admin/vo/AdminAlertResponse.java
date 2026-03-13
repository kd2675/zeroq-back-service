package com.zeroq.back.service.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAlertResponse {
    private String id;
    private String type;
    private String severity;
    private String title;
    private String description;
    private String createdAt;
    private Long spaceId;
    private String sensorId;
    private String gatewayId;
}
