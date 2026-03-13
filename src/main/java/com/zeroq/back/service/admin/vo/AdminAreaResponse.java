package com.zeroq.back.service.admin.vo;

import com.zeroq.back.service.sensor.vo.SensorPlaceSnapshotDTO;
import com.zeroq.back.service.sensor.vo.SensorRecentTelemetryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAreaResponse {
    private Long spaceId;
    private String spaceCode;
    private String name;
    private String description;
    private String operationalStatus;
    private double averageRating;
    private long reviewCount;
    private boolean verified;
    private SensorPlaceSnapshotDTO snapshot;
    private double occupancyRate;
    private int occupiedCount;
    private int activeSensorCount;
    private String crowdLevel;
    private List<AdminSensorResponse> sensors;
    private List<SensorRecentTelemetryDTO> recentTelemetry;
    private int lowBatteryCount;
    private int offlineCount;
    private double avgBattery;
    private List<AdminTrendPointResponse> trend;
    private String addressLabel;
}
