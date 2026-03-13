package com.zeroq.back.service.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminWorkspaceResponse {
    private List<AdminAreaResponse> spaces;
    private List<AdminSensorResponse> sensors;
    private List<AdminGatewayResponse> gateways;
    private List<AdminAlertResponse> alerts;
    private List<AdminLogResponse> logs;
    private AdminDashboardSummaryResponse summary;
    private String generatedAt;
}
