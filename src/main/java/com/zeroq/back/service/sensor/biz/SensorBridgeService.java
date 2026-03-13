package com.zeroq.back.service.sensor.biz;

import auth.common.core.context.UserContext;
import com.zeroq.back.common.model.CrowdLevel;
import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.admin.entity.AdminGateway;
import com.zeroq.back.database.admin.entity.AdminSensor;
import com.zeroq.back.database.admin.entity.AdminSpace;
import com.zeroq.back.database.admin.repository.AdminGatewayRepository;
import com.zeroq.back.database.admin.repository.AdminSensorRepository;
import com.zeroq.back.service.sensor.vo.*;
import com.zeroq.back.service.space.biz.SpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SensorBridgeService {
    private final SpaceService spaceService;
    private final AdminSensorRepository adminSensorRepository;
    private final AdminGatewayRepository adminGatewayRepository;
    private final SensorRawDataService sensorRawDataService;

    public SensorSpaceOverviewResponse getManagerSpaceOverview(
            Long spaceId,
            boolean recalculate,
            int telemetryLimit,
            UserContext userContext
    ) {
        requireManagerOrAdmin(userContext);

        AdminSpace space = spaceService.getSpaceById(spaceId);
        int cappedTelemetryLimit = capTelemetryLimit(telemetryLimit);
        List<SensorDeviceDTO> sensors = getDevices(spaceId, userContext);
        List<SensorRecentTelemetryDTO> telemetryForSnapshot = getRecentTelemetryForSensors(
                sensors,
                Math.max(cappedTelemetryLimit, sensors.size()),
                userContext
        );
        List<SensorRecentTelemetryDTO> recentTelemetry = telemetryForSnapshot.stream()
                .limit(cappedTelemetryLimit)
                .toList();
        SensorPlaceSnapshotDTO snapshot = buildSnapshot(spaceId, telemetryForSnapshot);

        return SensorSpaceOverviewResponse.builder()
                .spaceId(space.getId())
                .spaceName(space.getName())
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

        AdminSpace space = spaceService.getSpaceById(spaceId);
        List<SensorDeviceDTO> sensors = adminSensorRepository.findAllBySpaceId(spaceId).stream()
                .map(this::toDeviceDto)
                .toList();
        List<SensorRecentTelemetryDTO> recentTelemetry = getRecentTelemetryForSensors(sensors, Math.max(20, sensors.size()), userContext);
        SensorPlaceSnapshotDTO snapshot = buildSnapshot(spaceId, recentTelemetry);

        return SensorPublicSnapshotResponse.builder()
                .spaceId(space.getId())
                .spaceName(space.getName())
                .snapshot(snapshot)
                .build();
    }

    public List<SensorDeviceDTO> getDevices(Long placeId, UserContext userContext) {
        requireManagerOrAdmin(userContext);
        List<AdminSensor> sensors = placeId == null
                ? adminSensorRepository.findAll()
                : adminSensorRepository.findAllBySpaceId(placeId);
        return sensors.stream()
                .map(this::toDeviceDto)
                .toList();
    }

    @Transactional
    public SensorDeviceDTO registerSensor(RegisterSensorDeviceRequest request, UserContext userContext) {
        requireManagerOrAdmin(userContext);
        AdminSensor existing = adminSensorRepository.findBySensorId(request.getSensorId()).orElse(null);
        validateMacAddress(existing, request.getMacAddress());
        GatewayPlacement placement = resolveGatewayPlacement(request.getPlaceId(), request.getGatewayId(), false);

        AdminSensor sensor = existing == null ? AdminSensor.builder().sensorId(request.getSensorId()).build() : existing;
        sensor.setMacAddress(request.getMacAddress());
        sensor.setModel(request.getModel());
        sensor.setFirmwareVersion(request.getFirmwareVersion());
        sensor.setType(normalizeText(request.getType(), "OCCUPANCY_DETECTION"));
        sensor.setProtocol(normalizeText(request.getProtocol(), "MQTT"));
        sensor.setStatus(existing == null ? "ACTIVE" : normalizeText(sensor.getStatus(), "ACTIVE"));
        sensor.setGatewayId(placement.gatewayId());
        sensor.setPositionCode(request.getPositionCode());
        sensor.setOccupancyThresholdCm(request.getOccupancyThresholdCm());
        sensor.setCalibrationOffsetCm(request.getCalibrationOffsetCm());
        sensor.setMetadataJson(request.getMetadataJson());

        AdminSensor saved = adminSensorRepository.save(sensor);
        return toDeviceDto(saved);
    }

    @Transactional
    public SensorDeviceDTO installSensor(
            String sensorId,
            InstallSensorDeviceRequest request,
            UserContext userContext
    ) {
        requireManagerOrAdmin(userContext);
        AdminSensor sensor = findAdminSensor(sensorId);
        GatewayPlacement placement = resolveGatewayPlacement(request.getPlaceId(), request.getGatewayId(), true);
        sensor.setGatewayId(placement.gatewayId());
        sensor.setPositionCode(request.getPositionCode());
        if (request.getOccupancyThresholdCm() != null) {
            sensor.setOccupancyThresholdCm(request.getOccupancyThresholdCm());
        }
        if (request.getCalibrationOffsetCm() != null) {
            sensor.setCalibrationOffsetCm(request.getCalibrationOffsetCm());
        }
        sensor.setStatus("ACTIVE");

        AdminSensor saved = adminSensorRepository.save(sensor);
        return toDeviceDto(saved);
    }

    @Transactional
    public SensorDeviceDTO updateSensorStatus(
            String sensorId,
            UpdateSensorDeviceStatusRequest request,
            UserContext userContext
    ) {
        requireManagerOrAdmin(userContext);
        AdminSensor sensor = findAdminSensor(sensorId);
        sensor.setStatus(normalizeText(request.getStatus(), sensor.getStatus()));
        return toDeviceDto(adminSensorRepository.save(sensor));
    }

    @Transactional
    public SensorCommandDTO createCommand(CreateSensorCommandRequest request, UserContext userContext) {
        requireManagerOrAdmin(userContext);
        String requestedBy = resolveRequestedBy(userContext);
        return sensorRawDataService.createCommand(request, requestedBy);
    }

    @Transactional
    public void deleteSensor(String sensorId, UserContext userContext) {
        requireManagerOrAdmin(userContext);
        AdminSensor sensor = findAdminSensor(sensorId);
        sensorRawDataService.deleteSensor(sensorId);
        adminSensorRepository.delete(sensor);
    }

    private int capTelemetryLimit(int telemetryLimit) {
        return Math.max(1, Math.min(telemetryLimit, 200));
    }

    private AdminSensor findAdminSensor(String sensorId) {
        return adminSensorRepository.findBySensorId(sensorId)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("AdminSensor", "sensorId", sensorId));
    }

    private void validateMacAddress(AdminSensor existing, String macAddress) {
        adminSensorRepository.findByMacAddress(macAddress).ifPresent(owner -> {
            if (existing == null || !Objects.equals(existing.getId(), owner.getId())) {
                throw new LiveSpaceException.ConflictException("MAC address already in use: " + macAddress);
            }
        });
    }

    private SensorDeviceDTO toDeviceDto(AdminSensor sensor) {
        SensorDeviceDTO dto = new SensorDeviceDTO();
        dto.setId(sensor.getId());
        dto.setSensorId(sensor.getSensorId());
        dto.setMacAddress(sensor.getMacAddress());
        dto.setModel(sensor.getModel());
        dto.setFirmwareVersion(sensor.getFirmwareVersion());
        dto.setType(sensor.getType());
        dto.setProtocol(sensor.getProtocol());
        dto.setStatus(sensor.getStatus());
        dto.setPlaceId(resolvePlaceId(sensor.getGatewayId()));
        dto.setGatewayId(sensor.getGatewayId());
        dto.setPositionCode(sensor.getPositionCode());
        dto.setBatteryPercent(sensor.getBatteryPercent());
        dto.setOccupancyThresholdCm(sensor.getOccupancyThresholdCm());
        dto.setCalibrationOffsetCm(sensor.getCalibrationOffsetCm());
        dto.setLastHeartbeatAt(sensor.getLastHeartbeatAt());
        dto.setLastSequenceNo(sensor.getLastSequenceNo());
        dto.setMetadataJson(sensor.getMetadataJson());
        dto.setCreateDate(sensor.getCreatedAt());
        dto.setUpdateDate(sensor.getUpdatedAt());
        return dto;
    }

    private String normalizeText(String value, String fallback) {
        if (!StringUtils.hasText(value)) {
            return fallback;
        }
        return value.trim().toUpperCase();
    }

    private GatewayPlacement resolveGatewayPlacement(Long requestedPlaceId, String requestedGatewayId, boolean requireGateway) {
        if (!StringUtils.hasText(requestedGatewayId)) {
            if (requireGateway) {
                throw new LiveSpaceException.ValidationException("gatewayId is required");
            }
            if (requestedPlaceId != null) {
                throw new LiveSpaceException.ValidationException("Sensor cannot be attached to a space without gatewayId");
            }
            return new GatewayPlacement(null, null);
        }

        String normalizedGatewayId = requestedGatewayId.trim();
        AdminGateway gateway = adminGatewayRepository.findByGatewayId(normalizedGatewayId)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("AdminGateway", "gatewayId", normalizedGatewayId));

        Long resolvedPlaceId = gateway.getSpaceId();
        if (requestedPlaceId != null) {
            if (resolvedPlaceId != null && !Objects.equals(resolvedPlaceId, requestedPlaceId)) {
                throw new LiveSpaceException.ValidationException(
                        "Gateway %s is already assigned to another space".formatted(normalizedGatewayId)
                );
            }
            if (resolvedPlaceId == null) {
                gateway.setSpaceId(requestedPlaceId);
                adminGatewayRepository.save(gateway);
                resolvedPlaceId = requestedPlaceId;
            }
        } else if (resolvedPlaceId == null) {
            throw new LiveSpaceException.ValidationException("placeId is required when gateway is not assigned to a space");
        }

        return new GatewayPlacement(resolvedPlaceId, gateway.getGatewayId());
    }

    private Long resolvePlaceId(String gatewayId) {
        if (!StringUtils.hasText(gatewayId)) {
            return null;
        }
        return adminGatewayRepository.findByGatewayId(gatewayId.trim())
                .map(AdminGateway::getSpaceId)
                .orElse(null);
    }

    private List<SensorRecentTelemetryDTO> getRecentTelemetryForSensors(
            List<SensorDeviceDTO> sensors,
            int limit,
            UserContext userContext
    ) {
        if (sensors.isEmpty()) {
            return List.of();
        }

        int perSensorLimit = Math.max(1, Math.min(10, limit));
        List<SensorRecentTelemetryDTO> merged = new ArrayList<>();
        for (SensorDeviceDTO sensor : sensors) {
            List<SensorRecentTelemetryDTO> telemetry = sensorRawDataService.getRecentTelemetryBySensorId(sensor.getSensorId(), perSensorLimit);
            telemetry.forEach(item -> item.setPlaceId(sensor.getPlaceId()));
            merged.addAll(telemetry);
        }

        return merged.stream()
                .sorted(Comparator.comparing(SensorRecentTelemetryDTO::getMeasuredAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .toList();
    }

    private SensorPlaceSnapshotDTO buildSnapshot(Long placeId, List<SensorRecentTelemetryDTO> recentTelemetry) {
        if (recentTelemetry.isEmpty()) {
            return emptySnapshot(placeId);
        }

        List<SensorRecentTelemetryDTO> latestBySensor = recentTelemetry.stream()
                .filter(telemetry -> telemetry.getSensorId() != null)
                .collect(java.util.stream.Collectors.toMap(
                        SensorRecentTelemetryDTO::getSensorId,
                        telemetry -> telemetry,
                        (left, right) -> {
                            LocalDateTime leftAt = left.getMeasuredAt();
                            LocalDateTime rightAt = right.getMeasuredAt();
                            if (leftAt == null) {
                                return right;
                            }
                            if (rightAt == null) {
                                return left;
                            }
                            return rightAt.isAfter(leftAt) ? right : left;
                        }
                ))
                .values()
                .stream()
                .toList();

        if (latestBySensor.isEmpty()) {
            return emptySnapshot(placeId);
        }

        int activeSensorCount = latestBySensor.size();
        int occupiedCount = (int) latestBySensor.stream()
                .filter(telemetry -> Boolean.TRUE.equals(telemetry.getOccupied()))
                .count();
        double occupancyRate = activeSensorCount == 0 ? 0.0 : occupiedCount * 100.0 / activeSensorCount;
        LocalDateTime lastMeasuredAt = latestBySensor.stream()
                .map(SensorRecentTelemetryDTO::getMeasuredAt)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        SensorPlaceSnapshotDTO snapshot = new SensorPlaceSnapshotDTO();
        snapshot.setPlaceId(placeId);
        snapshot.setOccupiedCount(occupiedCount);
        snapshot.setActiveSensorCount(activeSensorCount);
        snapshot.setOccupancyRate(occupancyRate);
        snapshot.setCrowdLevel(CrowdLevel.fromOccupancyPercentage((int) Math.round(occupancyRate)).name());
        snapshot.setLastMeasuredAt(lastMeasuredAt);
        snapshot.setLastCalculatedAt(LocalDateTime.now());
        snapshot.setSourceWindowSeconds(600);
        return snapshot;
    }

    private SensorPlaceSnapshotDTO emptySnapshot(Long placeId) {
        SensorPlaceSnapshotDTO snapshot = new SensorPlaceSnapshotDTO();
        snapshot.setPlaceId(placeId);
        snapshot.setOccupiedCount(0);
        snapshot.setActiveSensorCount(0);
        snapshot.setOccupancyRate(0.0);
        snapshot.setCrowdLevel("EMPTY");
        return snapshot;
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

    private record GatewayPlacement(Long placeId, String gatewayId) {
    }
}
