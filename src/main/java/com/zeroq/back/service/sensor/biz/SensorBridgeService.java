package com.zeroq.back.service.sensor.biz;

import auth.common.core.context.UserContext;
import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.pub.entity.Space;
import com.zeroq.back.service.sensor.vo.*;
import com.zeroq.back.service.space.biz.SpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SensorBridgeService {
    private final SpaceService spaceService;
    private final SensorBridgeClient sensorBridgeClient;

    public SensorSpaceOverviewResponse getManagerSpaceOverview(
            Long spaceId,
            boolean recalculate,
            int telemetryLimit,
            UserContext userContext
    ) {
        requireManagerOrAdmin(userContext);

        Space space = spaceService.getSpaceById(spaceId);
        int cappedTelemetryLimit = capTelemetryLimit(telemetryLimit);

        SensorPlaceSnapshotDTO snapshot = sensorBridgeClient.getManagerSnapshot(spaceId, recalculate, userContext);
        List<SensorDeviceDTO> sensors = sensorBridgeClient.getSensors(spaceId, userContext);
        List<SensorRecentTelemetryDTO> recentTelemetry = sensorBridgeClient.getRecentTelemetry(
                spaceId,
                cappedTelemetryLimit,
                userContext
        );

        return SensorSpaceOverviewResponse.builder()
                .spaceId(space.getId())
                .spaceName(space.getName())
                .capacity(space.getCapacity())
                .snapshot(snapshot)
                .sensors(sensors)
                .recentTelemetry(recentTelemetry)
                .build();
    }

    public SensorPublicSnapshotResponse getPublicSpaceSnapshot(
            Long spaceId,
            boolean recalculate,
            UserContext userContext
    ) {
        requireAuthenticated(userContext);

        Space space = spaceService.getSpaceById(spaceId);
        SensorPlaceSnapshotDTO snapshot = sensorBridgeClient.getUserSnapshot(spaceId, recalculate, userContext);

        return SensorPublicSnapshotResponse.builder()
                .spaceId(space.getId())
                .spaceName(space.getName())
                .capacity(space.getCapacity())
                .snapshot(snapshot)
                .build();
    }

    public List<SensorDeviceDTO> getDevices(Long placeId, UserContext userContext) {
        requireManagerOrAdmin(userContext);
        return sensorBridgeClient.getSensors(placeId, userContext);
    }

    @Transactional
    public SensorDeviceDTO registerSensor(RegisterSensorDeviceRequest request, UserContext userContext) {
        requireManagerOrAdmin(userContext);
        return sensorBridgeClient.registerSensor(request, userContext);
    }

    @Transactional
    public SensorDeviceDTO installSensor(
            String sensorId,
            InstallSensorDeviceRequest request,
            UserContext userContext
    ) {
        requireManagerOrAdmin(userContext);
        return sensorBridgeClient.installSensor(sensorId, request, userContext);
    }

    @Transactional
    public SensorDeviceDTO updateSensorStatus(
            String sensorId,
            UpdateSensorDeviceStatusRequest request,
            UserContext userContext
    ) {
        requireManagerOrAdmin(userContext);
        return sensorBridgeClient.updateSensorStatus(sensorId, request, userContext);
    }

    @Transactional
    public SensorCommandDTO createCommand(CreateSensorCommandRequest request, UserContext userContext) {
        requireManagerOrAdmin(userContext);
        String requestedBy = resolveRequestedBy(userContext);
        return sensorBridgeClient.createCommand(request, requestedBy, userContext);
    }

    private int capTelemetryLimit(int telemetryLimit) {
        return Math.max(1, Math.min(telemetryLimit, 200));
    }

    private String resolveRequestedBy(UserContext userContext) {
        if (StringUtils.hasText(userContext.getUserName())) {
            return userContext.getUserName();
        }
        if (StringUtils.hasText(userContext.getUserKey())) {
            return userContext.getUserKey();
        }
        return "unknown-manager";
    }

    private void requireAuthenticated(UserContext userContext) {
        if (userContext == null || !userContext.isAuthenticated()) {
            throw new LiveSpaceException.UnauthorizedException("Login required");
        }
        if (!StringUtils.hasText(userContext.getRole())) {
            throw new LiveSpaceException.ForbiddenException("Missing role information");
        }
    }

    private void requireManagerOrAdmin(UserContext userContext) {
        requireAuthenticated(userContext);
        if (!userContext.isManager() && !userContext.isAdmin()) {
            throw new LiveSpaceException.ForbiddenException("MANAGER or ADMIN role required");
        }
    }
}
