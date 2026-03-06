package com.zeroq.back.service.sensor.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SensorPublicSnapshotResponse {
    private Long spaceId;
    private String spaceName;
    private Integer capacity;
    private SensorPlaceSnapshotDTO snapshot;
}
