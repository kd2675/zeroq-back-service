package com.zeroq.back.service.sensor.vo;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SensorSpaceOverviewResponse {
    private Long spaceId;
    private String spaceName;
    private SensorPlaceSnapshotDTO snapshot;
    private List<SensorDeviceDTO> sensors;
    private List<SensorRecentTelemetryDTO> recentTelemetry;
}
