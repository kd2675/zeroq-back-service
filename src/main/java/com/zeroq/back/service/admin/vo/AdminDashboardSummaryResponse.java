package com.zeroq.back.service.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardSummaryResponse {
    private int occupiedNow;
    private double occupancyRate;
    private int activeSensors;
    private int offlineSensors;
    private double gatewayHealth;
    private double peakRate;
}
