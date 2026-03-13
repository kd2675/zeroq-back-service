package com.zeroq.back.service.admin.biz;

import auth.common.core.context.UserContext;
import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.admin.entity.AdminGateway;
import com.zeroq.back.database.admin.entity.Notification;
import com.zeroq.back.database.admin.entity.NotificationPreference;
import com.zeroq.back.database.admin.entity.UserPreference;
import com.zeroq.back.database.admin.repository.AdminGatewayRepository;
import com.zeroq.back.database.sensor.entity.GatewayStatusSnapshot;
import com.zeroq.back.database.admin.repository.NotificationPreferenceRepository;
import com.zeroq.back.database.admin.repository.NotificationRepository;
import com.zeroq.back.database.admin.repository.UserPreferenceRepository;
import com.zeroq.back.database.admin.entity.AdminSpace;
import com.zeroq.back.database.pub.repository.ReviewRepository;
import com.zeroq.back.service.admin.vo.AdminAlertResponse;
import com.zeroq.back.service.admin.vo.AdminAreaResponse;
import com.zeroq.back.service.admin.vo.AdminConsoleSettingsResponse;
import com.zeroq.back.service.admin.vo.AdminDashboardSummaryResponse;
import com.zeroq.back.service.admin.vo.AdminGatewayResponse;
import com.zeroq.back.service.admin.vo.AdminLogResponse;
import com.zeroq.back.service.admin.vo.AdminSensorResponse;
import com.zeroq.back.service.admin.vo.AdminTrendPointResponse;
import com.zeroq.back.service.admin.vo.AdminWorkspaceResponse;
import com.zeroq.back.service.admin.vo.UpdateAdminConsoleSettingsRequest;
import com.zeroq.back.service.sensor.biz.SensorBridgeService;
import com.zeroq.back.service.sensor.biz.GatewayRuntimeSnapshotService;
import com.zeroq.back.service.sensor.vo.SensorDeviceDTO;
import com.zeroq.back.service.sensor.vo.SensorPlaceSnapshotDTO;
import com.zeroq.back.service.sensor.vo.SensorRecentTelemetryDTO;
import com.zeroq.back.service.sensor.vo.SensorSpaceOverviewResponse;
import com.zeroq.back.service.space.biz.SpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminConsoleService {
    private static final int MAX_SPACES = 24;
    private static final int HISTORY_POINTS = 7;
    private static final int TELEMETRY_LIMIT = 12;
    private static final DateTimeFormatter HISTORY_LABEL_FORMAT = DateTimeFormatter.ofPattern("HH", Locale.KOREA);
    private static final String DEFAULT_SENSOR_RETENTION = "90 Days";
    private static final String DEFAULT_ERROR_RETENTION = "30 Days";
    private static final String DEFAULT_ALERT_RETENTION = "1 Year";
    private static final int DEFAULT_OVERCAPACITY_LIMIT = 150;
    private static final int DEFAULT_WARNING_BUFFER_PERCENT = 85;

    private final SpaceService spaceService;
    private final SensorBridgeService sensorBridgeService;
    private final GatewayRuntimeSnapshotService gatewayRuntimeSnapshotService;
    private final AdminGatewayRepository adminGatewayRepository;
    private final ReviewRepository reviewRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final AdminProfileService adminProfileService;

    public AdminWorkspaceResponse getWorkspace(UserContext userContext) {
        requireManagerOrAdmin(userContext);
        Long profileId = resolveProfileId(userContext);

        List<AdminSpace> spaces = spaceService.getWorkspaceManagedSpaces(
                profileId,
                userContext.isAdmin(),
                PageRequest.of(0, MAX_SPACES)
        ).getContent();
        if (spaces.isEmpty()) {
            return emptyWorkspace();
        }

        Map<Long, List<AdminGateway>> gatewaysBySpaceId = adminGatewayRepository.findAll().stream()
                .filter(gateway -> gateway.getSpaceId() != null)
                .collect(Collectors.groupingBy(AdminGateway::getSpaceId));
        List<AdminAreaResponse> areas = new ArrayList<>();
        for (int index = 0; index < spaces.size(); index++) {
            AdminSpace space = spaces.get(index);
            areas.add(buildAreaResponse(
                    space,
                    userContext
            ));
        }

        List<AdminGatewayResponse> gateways = buildGatewayResponses(areas, gatewaysBySpaceId);
        List<AdminSensorResponse> sensors = areas.stream()
                .flatMap(area -> area.getSensors().stream())
                .toList();
        List<AdminAlertResponse> alerts = buildAlerts(areas, sensors, gateways);
        List<AdminLogResponse> logs = buildLogs(alerts, sensors, gateways, profileId);

        int occupiedNow = areas.stream().mapToInt(AdminAreaResponse::getOccupiedCount).sum();
        long activeSensors = sensors.stream().filter(sensor -> "ACTIVE".equals(sensor.getStatus())).count();
        long offlineSensors = sensors.size() - activeSensors;
        long onlineGateways = gateways.stream().filter(gateway -> "Online".equals(gateway.getStatus())).count();
        double occupancyRate = areas.stream()
                .mapToDouble(AdminAreaResponse::getOccupancyRate)
                .average()
                .orElse(0.0);
        double peakRate = areas.stream()
                .mapToDouble(AdminAreaResponse::getOccupancyRate)
                .max()
                .orElse(0.0);

        return AdminWorkspaceResponse.builder()
                .spaces(areas.stream()
                        .sorted(Comparator.comparingDouble(AdminAreaResponse::getOccupancyRate).reversed())
                        .toList())
                .sensors(sensors)
                .gateways(gateways)
                .alerts(alerts)
                .logs(logs)
                .summary(AdminDashboardSummaryResponse.builder()
                        .occupiedNow(occupiedNow)
                        .occupancyRate(occupancyRate)
                        .activeSensors((int) activeSensors)
                        .offlineSensors((int) offlineSensors)
                        .gatewayHealth(gateways.isEmpty() ? 0.0 : onlineGateways * 100.0 / gateways.size())
                        .peakRate(peakRate)
                        .build())
                .generatedAt(LocalDateTime.now().toString())
                .build();
    }

    public AdminConsoleSettingsResponse getSettings(UserContext userContext) {
        requireManagerOrAdmin(userContext);
        Long profileId = resolveProfileId(userContext);

        NotificationPreference notificationPreference = notificationPreferenceRepository.findByProfileId(profileId)
                .orElseGet(() -> defaultNotificationPreference(profileId));
        Map<String, String> preferenceMap = userPreferenceRepository.findByProfileId(profileId).stream()
                .collect(Collectors.toMap(UserPreference::getPreferenceKey, UserPreference::getPreferenceValue, (left, right) -> right));

        return AdminConsoleSettingsResponse.builder()
                .overcapacityLimit(getIntPreference(preferenceMap, UserPreference.Keys.ADMIN_OVERCAPACITY_LIMIT, DEFAULT_OVERCAPACITY_LIMIT))
                .warningBufferPercent(getIntPreference(preferenceMap, UserPreference.Keys.ADMIN_WARNING_BUFFER_PERCENT, DEFAULT_WARNING_BUFFER_PERCENT))
                .sensorRawDataRetention(preferenceMap.getOrDefault(
                        UserPreference.Keys.ADMIN_SENSOR_RAW_DATA_RETENTION,
                        DEFAULT_SENSOR_RETENTION
                ))
                .systemErrorRetention(preferenceMap.getOrDefault(
                        UserPreference.Keys.ADMIN_SYSTEM_ERROR_RETENTION,
                        DEFAULT_ERROR_RETENTION
                ))
                .alertHistoryRetention(preferenceMap.getOrDefault(
                        UserPreference.Keys.ADMIN_ALERT_HISTORY_RETENTION,
                        DEFAULT_ALERT_RETENTION
                ))
                .allNotificationsEnabled(notificationPreference.isAllNotificationsEnabled())
                .occupancyNotificationsEnabled(notificationPreference.isOccupancyNotificationsEnabled())
                .batteryNotificationsEnabled(notificationPreference.isBatteryNotificationsEnabled())
                .emailNotificationsEnabled(notificationPreference.isEmailNotificationsEnabled())
                .pushNotificationsEnabled(notificationPreference.isPushNotificationsEnabled())
                .smsNotificationsEnabled(notificationPreference.isSmsNotificationsEnabled())
                .managedByProfileId(profileId)
                .role(userContext.getRole())
                .build();
    }

    @Transactional(transactionManager = "adminTransactionManager")
    public AdminConsoleSettingsResponse updateSettings(
            UpdateAdminConsoleSettingsRequest request,
            UserContext userContext
    ) {
        requireManagerOrAdmin(userContext);
        validateSettings(request);
        Long profileId = resolveProfileId(userContext);

        NotificationPreference notificationPreference = notificationPreferenceRepository.findByProfileId(profileId)
                .orElseGet(() -> defaultNotificationPreference(profileId));
        notificationPreference.setAllNotificationsEnabled(request.isAllNotificationsEnabled());
        notificationPreference.setOccupancyNotificationsEnabled(request.isOccupancyNotificationsEnabled());
        notificationPreference.setBatteryNotificationsEnabled(request.isBatteryNotificationsEnabled());
        notificationPreference.setEmailNotificationsEnabled(request.isEmailNotificationsEnabled());
        notificationPreference.setPushNotificationsEnabled(request.isPushNotificationsEnabled());
        notificationPreference.setSmsNotificationsEnabled(request.isSmsNotificationsEnabled());
        notificationPreferenceRepository.save(notificationPreference);

        upsertPreference(profileId, UserPreference.Keys.ADMIN_OVERCAPACITY_LIMIT, String.valueOf(request.getOvercapacityLimit()),
                "Admin overcapacity limit");
        upsertPreference(profileId, UserPreference.Keys.ADMIN_WARNING_BUFFER_PERCENT, String.valueOf(request.getWarningBufferPercent()),
                "Admin warning buffer percent");
        upsertPreference(profileId, UserPreference.Keys.ADMIN_SENSOR_RAW_DATA_RETENTION, request.getSensorRawDataRetention(),
                "Admin sensor raw data retention");
        upsertPreference(profileId, UserPreference.Keys.ADMIN_SYSTEM_ERROR_RETENTION, request.getSystemErrorRetention(),
                "Admin system error retention");
        upsertPreference(profileId, UserPreference.Keys.ADMIN_ALERT_HISTORY_RETENTION, request.getAlertHistoryRetention(),
                "Admin alert history retention");

        return getSettings(userContext);
    }

    private AdminAreaResponse buildAreaResponse(
            AdminSpace space,
            UserContext userContext
    ) {
        SensorSpaceOverviewResponse overview = fetchOverview(space.getId(), userContext).orElse(null);
        SensorPlaceSnapshotDTO snapshot = overview != null ? overview.getSnapshot() : null;
        List<SensorRecentTelemetryDTO> recentTelemetry = overview != null && overview.getRecentTelemetry() != null
                ? overview.getRecentTelemetry()
                : List.of();
        double occupancyRate = snapshot != null && snapshot.getOccupancyRate() != null ? snapshot.getOccupancyRate() : 0.0;
        int occupiedCount = snapshot != null && snapshot.getOccupiedCount() != null ? snapshot.getOccupiedCount() : 0;
        int activeSensorCount = snapshot != null && snapshot.getActiveSensorCount() != null ? snapshot.getActiveSensorCount() : 0;
        String crowdLevel = snapshot != null && StringUtils.hasText(snapshot.getCrowdLevel()) ? snapshot.getCrowdLevel() : "LOW";
        long reviewCount = reviewRepository.countReviewsBySpace(space.getId());

        List<AdminSensorResponse> sensors = new ArrayList<>();
        if (overview != null && overview.getSensors() != null) {
            for (int sensorIndex = 0; sensorIndex < overview.getSensors().size(); sensorIndex++) {
                SensorDeviceDTO sensor = overview.getSensors().get(sensorIndex);
                sensors.add(buildSensorResponse(
                        space,
                        occupancyRate,
                        recentTelemetry,
                        sensor
                ));
            }
        }

        double avgBattery = sensors.stream()
                .map(AdminSensorResponse::getBatteryPercent)
                .filter(value -> value != null)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        return AdminAreaResponse.builder()
                .spaceId(space.getId())
                .spaceCode(space.getSpaceCode())
                .name(space.getName())
                .description(space.getDescription())
                .operationalStatus(space.getOperationalStatus())
                .averageRating(space.getAverageRating() != null ? space.getAverageRating() : 0.0)
                .reviewCount(reviewCount)
                .verified(space.isVerified())
                .snapshot(snapshot)
                .occupancyRate(occupancyRate)
                .occupiedCount(occupiedCount)
                .activeSensorCount(activeSensorCount)
                .crowdLevel(crowdLevel)
                .sensors(sensors)
                .recentTelemetry(recentTelemetry)
                .lowBatteryCount((int) sensors.stream()
                        .filter(sensor -> sensor.getBatteryPercent() != null && sensor.getBatteryPercent() <= 15.0)
                        .count())
                .offlineCount((int) sensors.stream().filter(sensor -> !"ACTIVE".equals(sensor.getStatus())).count())
                .avgBattery(avgBattery)
                .trend(buildTrend(occupancyRate, recentTelemetry))
                .addressLabel(space.getLocation() != null && StringUtils.hasText(space.getLocation().getAddress())
                        ? space.getLocation().getAddress()
                        : null)
                .build();
    }

    private Optional<SensorSpaceOverviewResponse> fetchOverview(Long spaceId, UserContext userContext) {
        try {
            return Optional.of(sensorBridgeService.getManagerSpaceOverview(spaceId, false, TELEMETRY_LIMIT, userContext));
        } catch (RuntimeException ignored) {
            return Optional.empty();
        }
    }

    private AdminSensorResponse buildSensorResponse(
            AdminSpace space,
            double occupancyRate,
            List<SensorRecentTelemetryDTO> recentTelemetry,
            SensorDeviceDTO sensor
    ) {
        Double batteryPercent = sensor.getBatteryPercent();
        String detectionStatus = resolveDetectionStatus(sensor, recentTelemetry);
        String gatewayId = resolveGatewayId(sensor);

        return AdminSensorResponse.builder()
                .id(sensor.getId())
                .sensorId(sensor.getSensorId())
                .macAddress(sensor.getMacAddress())
                .model(sensor.getModel())
                .firmwareVersion(sensor.getFirmwareVersion())
                .type(sensor.getType())
                .protocol(sensor.getProtocol())
                .status(sensor.getStatus())
                .placeId(sensor.getPlaceId() != null ? sensor.getPlaceId() : space.getId())
                .positionCode(sensor.getPositionCode())
                .batteryPercent(batteryPercent)
                .lastHeartbeatAt(sensor.getLastHeartbeatAt())
                .lastSequenceNo(sensor.getLastSequenceNo())
                .spaceName(space.getName())
                .occupancyRate(occupancyRate)
                .gatewayId(gatewayId)
                .detectionStatus(detectionStatus)
                .signalStrength(null)
                .locationLabel(StringUtils.hasText(sensor.getPositionCode()) ? sensor.getPositionCode() : null)
                .batteryLabel(resolveBatteryLabel(batteryPercent))
                .build();
    }

    private List<AdminTrendPointResponse> buildTrend(
            double occupancyRate,
            List<SensorRecentTelemetryDTO> recentTelemetry
    ) {
        if (recentTelemetry != null && !recentTelemetry.isEmpty()) {
            List<SensorRecentTelemetryDTO> sortedTelemetry = recentTelemetry.stream()
                    .sorted(Comparator.comparing(
                            SensorRecentTelemetryDTO::getMeasuredAt,
                            Comparator.nullsLast(Comparator.naturalOrder())
                    ))
                    .toList();

            int fromIndex = Math.max(0, sortedTelemetry.size() - HISTORY_POINTS);
            List<SensorRecentTelemetryDTO> window = sortedTelemetry.subList(fromIndex, sortedTelemetry.size());

            return window.stream()
                    .map(item -> {
                        double delta = Boolean.TRUE.equals(item.getOccupied()) ? 12.0 : -14.0;
                        double value = clamp(Math.round((occupancyRate > 0.0 ? occupancyRate : 42.0) + delta), 8, 99);
                        String label = item.getMeasuredAt() != null
                                ? item.getMeasuredAt().format(HISTORY_LABEL_FORMAT)
                                : "--";
                        return AdminTrendPointResponse.builder()
                                .label(label)
                                .value(value)
                                .build();
                    })
                    .toList();
        }

        return List.of();
    }

    private List<AdminGatewayResponse> buildGatewayResponses(
            List<AdminAreaResponse> areas,
            Map<Long, List<AdminGateway>> gatewaysBySpaceId
    ) {
        List<AdminGatewayResponse> gateways = new ArrayList<>();
        Map<String, GatewayStatusSnapshot> snapshotsByGatewayId = gatewayRuntimeSnapshotService.getSnapshots(
                gatewaysBySpaceId.values().stream()
                        .flatMap(List::stream)
                        .map(AdminGateway::getGatewayId)
                        .filter(StringUtils::hasText)
                        .toList()
        );

        for (AdminAreaResponse area : areas) {
            List<AdminGateway> registeredGateways = gatewaysBySpaceId.getOrDefault(area.getSpaceId(), List.of());
            for (AdminGateway registeredGateway : registeredGateways) {
                List<AdminSensorResponse> connectedSensors = area.getSensors().stream()
                        .filter(sensor -> registeredGateway.getGatewayId().equals(sensor.getGatewayId()))
                        .toList();
                gateways.add(toGatewayResponse(
                        area,
                        registeredGateway,
                        snapshotsByGatewayId.get(registeredGateway.getGatewayId()),
                        connectedSensors
                ));
            }
        }

        gateways.sort(Comparator
                .comparing((AdminGatewayResponse gateway) -> "Offline".equals(gateway.getStatus()) ? 0 : 1)
                .thenComparing(AdminGatewayResponse::getGatewayId, Comparator.nullsLast(String::compareToIgnoreCase)));
        return gateways;
    }

    private List<AdminAlertResponse> buildAlerts(
            List<AdminAreaResponse> areas,
            List<AdminSensorResponse> sensors,
            List<AdminGatewayResponse> gateways
    ) {
        List<AdminAlertResponse> alerts = new ArrayList<>();

        for (AdminAreaResponse area : areas) {
            if (area.getOccupancyRate() >= 90.0) {
                alerts.add(AdminAlertResponse.builder()
                        .id("capacity-%d".formatted(area.getSpaceId()))
                        .type("Capacity Alert")
                        .severity("critical")
                        .title("%s over %d%%".formatted(area.getName(), Math.round(area.getOccupancyRate())))
                        .description("%d occupants detected with sustained %d%% utilization.".formatted(
                                area.getOccupiedCount(),
                                Math.round(area.getOccupancyRate())
                        ))
                        .createdAt(area.getSnapshot() != null && area.getSnapshot().getLastMeasuredAt() != null
                                ? area.getSnapshot().getLastMeasuredAt().toString()
                                : LocalDateTime.now().toString())
                        .spaceId(area.getSpaceId())
                        .build());
            }

            if (area.getLowBatteryCount() > 0) {
                alerts.add(AdminAlertResponse.builder()
                        .id("battery-%d".formatted(area.getSpaceId()))
                        .type("Battery Watch")
                        .severity("warning")
                        .title("%s low battery sensors".formatted(area.getName()))
                        .description("%d sensor(s) are below 15%% battery.".formatted(area.getLowBatteryCount()))
                        .createdAt(area.getSnapshot() != null && area.getSnapshot().getLastMeasuredAt() != null
                                ? area.getSnapshot().getLastMeasuredAt().toString()
                                : LocalDateTime.now().toString())
                        .spaceId(area.getSpaceId())
                        .build());
            }
        }

        sensors.stream()
                .filter(sensor -> !"ACTIVE".equals(sensor.getStatus()))
                .limit(4)
                .forEach(new Consumer<>() {
                    private int index = 0;

                    @Override
                    public void accept(AdminSensorResponse sensor) {
                        alerts.add(AdminAlertResponse.builder()
                                .id("sensor-%s-%d".formatted(sensor.getSensorId(), index))
                                .type("Sensor Offline")
                                .severity("MAINTENANCE".equals(sensor.getStatus()) ? "warning" : "critical")
                                .title("%s requires attention".formatted(sensor.getSensorId()))
                                .description("%s · %s".formatted(sensor.getSpaceName(), sensor.getLocationLabel()))
                                .createdAt(sensor.getLastHeartbeatAt() != null
                                        ? sensor.getLastHeartbeatAt().toString()
                                        : LocalDateTime.now().toString())
                                .spaceId(sensor.getPlaceId())
                                .sensorId(sensor.getSensorId())
                                .gatewayId(sensor.getGatewayId())
                                .build());
                        index++;
                    }
                });

        gateways.stream()
                .filter(gateway -> "Offline".equals(gateway.getStatus()))
                .limit(3)
                .forEach(new Consumer<>() {
                    private int index = 0;

                    @Override
                    public void accept(AdminGatewayResponse gateway) {
                        alerts.add(AdminAlertResponse.builder()
                                .id("gateway-%s-%d".formatted(gateway.getGatewayId(), index))
                                .type("Gateway Alert")
                                .severity("critical")
                                .title("%s lost heartbeat".formatted(gateway.getGatewayId()))
                                .description(StringUtils.hasText(gateway.getLinkedBridge())
                                        ? "%s · %s".formatted(gateway.getSpaceName(), gateway.getLinkedBridge())
                                        : gateway.getSpaceName())
                                .createdAt(gateway.getLastHeartbeatAt() != null
                                        ? gateway.getLastHeartbeatAt().toString()
                                        : LocalDateTime.now().toString())
                                .spaceId(gateway.getSpaceId())
                                .gatewayId(gateway.getGatewayId())
                                .build());
                        index++;
                    }
                });

        alerts.sort(Comparator.comparing(AdminAlertResponse::getCreatedAt).reversed());
        return alerts.stream().limit(12).toList();
    }

    private List<AdminLogResponse> buildLogs(
            List<AdminAlertResponse> alerts,
            List<AdminSensorResponse> sensors,
            List<AdminGatewayResponse> gateways,
            Long profileId
    ) {
        List<AdminLogResponse> logs = new ArrayList<>();

        for (AdminAlertResponse alert : alerts) {
            logs.add(AdminLogResponse.builder()
                    .id("log-%s".formatted(alert.getId()))
                    .timestamp(alert.getCreatedAt())
                    .eventType(alert.getType())
                    .targetLabel(alert.getSensorId() != null
                            ? alert.getSensorId()
                            : alert.getGatewayId() != null
                            ? alert.getGatewayId()
                            : alert.getSpaceId() != null ? "Space %d".formatted(alert.getSpaceId()) : "Console")
                    .severity(alert.getSeverity())
                    .details(alert.getDescription())
                    .spaceId(alert.getSpaceId())
                    .sensorId(alert.getSensorId())
                    .gatewayId(alert.getGatewayId())
                    .build());
        }

        List<Notification> notifications = notificationRepository
                .findByProfileIdOrderByCreateDateDesc(profileId, PageRequest.of(0, 8))
                .getContent();
        for (Notification notification : notifications) {
            logs.add(AdminLogResponse.builder()
                    .id("notification-%d".formatted(notification.getId()))
                    .timestamp(notification.getCreateDate() != null ? notification.getCreateDate().toString() : LocalDateTime.now().toString())
                    .eventType("Notification")
                    .targetLabel(notification.getRelatedEntityType() != null ? notification.getRelatedEntityType() : "Notification")
                    .severity("SYSTEM".equals(notification.getType().name()) ? "info" : "warning")
                    .details("%s · %s".formatted(notification.getTitle(), notification.getMessage()))
                    .build());
        }

        for (int index = 0; index < Math.min(8, sensors.size()); index++) {
            AdminSensorResponse sensor = sensors.get(index);
            logs.add(AdminLogResponse.builder()
                    .id("sensor-log-%s-%d".formatted(sensor.getSensorId(), index))
                    .timestamp(sensor.getLastHeartbeatAt() != null
                            ? sensor.getLastHeartbeatAt().toString()
                            : LocalDateTime.now().minusMinutes((index + 1L) * 11).toString())
                    .eventType("Sensor Heartbeat")
                    .targetLabel(sensor.getSensorId())
                    .severity("ACTIVE".equals(sensor.getStatus()) ? "info" : "warning")
                    .details(formatSensorLogDetails(sensor))
                    .spaceId(sensor.getPlaceId())
                    .sensorId(sensor.getSensorId())
                    .gatewayId(sensor.getGatewayId())
                    .build());
        }

        for (int index = 0; index < Math.min(8, gateways.size()); index++) {
            AdminGatewayResponse gateway = gateways.get(index);
            logs.add(AdminLogResponse.builder()
                    .id("gateway-log-%s-%d".formatted(gateway.getGatewayId(), index))
                    .timestamp(gateway.getLastHeartbeatAt() != null
                            ? gateway.getLastHeartbeatAt().toString()
                            : LocalDateTime.now().minusMinutes((index + 1L) * 17).toString())
                    .eventType("Gateway Sync")
                    .targetLabel(gateway.getGatewayId())
                    .severity("Online".equals(gateway.getStatus()) ? "success" : "critical")
                    .details(formatGatewayLogDetails(gateway))
                    .spaceId(gateway.getSpaceId())
                    .gatewayId(gateway.getGatewayId())
                    .build());
        }

        logs.sort(Comparator.comparing(AdminLogResponse::getTimestamp).reversed());
        return logs.stream().limit(24).toList();
    }

    private String resolveGatewayId(
            SensorDeviceDTO sensor
    ) {
        return sensor.getGatewayId();
    }

    private AdminGatewayResponse toGatewayResponse(
            AdminAreaResponse area,
            AdminGateway gateway,
            GatewayStatusSnapshot snapshot,
            List<AdminSensorResponse> connectedSensors
    ) {
        String status = normalizeGatewayStatus(snapshot != null ? snapshot.getStatus() : gateway.getStatus());
        return AdminGatewayResponse.builder()
                .gatewayId(gateway.getGatewayId())
                .gatewayName(gateway.getGatewayName())
                .gatewayRole(gateway.getGatewayRole())
                .regionCode(gateway.getRegionCode())
                .spaceId(area.getSpaceId())
                .spaceName(area.getName())
                .locationLabel(gateway.getLocationLabel())
                .ipAddress(StringUtils.hasText(snapshot != null ? snapshot.getIpAddress() : null)
                        ? snapshot.getIpAddress()
                        : gateway.getIpAddress())
                .sensorCapacity(gateway.getSensorCapacity())
                .currentSensorLoad(snapshot != null ? snapshot.getCurrentSensorLoad() : gateway.getCurrentSensorLoad())
                .firmwareVersion(StringUtils.hasText(snapshot != null ? snapshot.getFirmwareVersion() : null)
                        ? snapshot.getFirmwareVersion()
                        : gateway.getFirmwareVersion())
                .signalStrength(null)
                .throughputMbps(snapshot != null ? snapshot.getCurrentSensorLoad() : gateway.getCurrentSensorLoad())
                .status(status)
                .linkedBridge(gateway.getLinkedBridge())
                .latencyMs(snapshot != null ? snapshot.getLatencyMs() : gateway.getLatencyMs())
                .packetLossPercent(snapshot != null ? snapshot.getPacketLossPercent() : gateway.getPacketLossPercent())
                .connectedSensors(connectedSensors)
                .lastHeartbeatAt(snapshot != null ? snapshot.getLastHeartbeatAt() : gateway.getLastHeartbeatAt())
                .build();
    }

    private String normalizeGatewayStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return "Unknown";
        }
        String normalized = status.trim().toUpperCase(Locale.ROOT);
        if (normalized.startsWith("ON")) {
            return "Online";
        }
        if (normalized.startsWith("OFF")) {
            return "Offline";
        }
        return status;
    }

    private NotificationPreference defaultNotificationPreference(Long profileId) {
        return NotificationPreference.builder()
                .profileId(profileId)
                .allNotificationsEnabled(true)
                .occupancyNotificationsEnabled(true)
                .batteryNotificationsEnabled(true)
                .reviewNotificationsEnabled(true)
                .promotionNotificationsEnabled(true)
                .emailNotificationsEnabled(false)
                .pushNotificationsEnabled(true)
                .smsNotificationsEnabled(false)
                .build();
    }

    private void upsertPreference(Long profileId, String preferenceKey, String preferenceValue, String description) {
        UserPreference preference = userPreferenceRepository.findByProfileIdAndPreferenceKey(profileId, preferenceKey)
                .orElseGet(() -> UserPreference.builder()
                        .profileId(profileId)
                        .preferenceKey(preferenceKey)
                        .description(description)
                        .build());
        preference.setPreferenceValue(preferenceValue);
        preference.setDescription(description);
        userPreferenceRepository.save(preference);
    }

    private int getIntPreference(Map<String, String> preferences, String key, int defaultValue) {
        String rawValue = preferences.get(key);
        if (!StringUtils.hasText(rawValue)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(rawValue);
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    private void validateSettings(UpdateAdminConsoleSettingsRequest request) {
        if (request.getOvercapacityLimit() <= 0) {
            throw new LiveSpaceException.ValidationException("Overcapacity limit must be greater than zero");
        }
        if (request.getWarningBufferPercent() <= 0 || request.getWarningBufferPercent() > 100) {
            throw new LiveSpaceException.ValidationException("Warning buffer percent must be between 1 and 100");
        }
    }

    private String resolveDetectionStatus(SensorDeviceDTO sensor, List<SensorRecentTelemetryDTO> recentTelemetry) {
        if (!"ACTIVE".equals(sensor.getStatus())) {
            return "Offline";
        }

        return recentTelemetry.stream()
                .filter(telemetry -> sensor.getSensorId().equals(telemetry.getSensorId()))
                .findFirst()
                .map(telemetry -> Boolean.TRUE.equals(telemetry.getOccupied()) ? "Occupied" : "Vacant")
                .orElse("Vacant");
    }

    private String resolveBatteryLabel(Double batteryPercent) {
        if (batteryPercent == null) {
            return "Unknown";
        }
        if (batteryPercent <= 15.0) {
            return "Critical";
        }
        if (batteryPercent <= 40.0) {
            return "Watch";
        }
        return "%d%%".formatted(Math.round(batteryPercent));
    }

    private String formatSensorLogDetails(AdminSensorResponse sensor) {
        List<String> parts = new ArrayList<>();
        parts.add(sensor.getSpaceName());
        if (StringUtils.hasText(sensor.getLocationLabel())) {
            parts.add(sensor.getLocationLabel());
        }
        parts.add(sensor.getBatteryLabel());
        return String.join(" · ", parts);
    }

    private String formatGatewayLogDetails(AdminGatewayResponse gateway) {
        List<String> parts = new ArrayList<>();
        parts.add(gateway.getSpaceName());
        parts.add("%d sensors".formatted(gateway.getConnectedSensors() != null ? gateway.getConnectedSensors().size() : 0));
        if (StringUtils.hasText(gateway.getLinkedBridge())) {
            parts.add(gateway.getLinkedBridge());
        }
        return String.join(" · ", parts);
    }

    private double clamp(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    private AdminWorkspaceResponse emptyWorkspace() {
        return AdminWorkspaceResponse.builder()
                .spaces(List.of())
                .sensors(List.of())
                .gateways(List.of())
                .alerts(List.of())
                .logs(List.of())
                .summary(AdminDashboardSummaryResponse.builder()
                        .occupiedNow(0)
                        .occupancyRate(0.0)
                        .activeSensors(0)
                        .offlineSensors(0)
                        .gatewayHealth(0.0)
                        .peakRate(0.0)
                        .build())
                .generatedAt(LocalDateTime.now().toString())
                .build();
    }

    private void requireManagerOrAdmin(UserContext userContext) {
        if (userContext == null || !userContext.isAuthenticated()) {
            throw new LiveSpaceException.UnauthorizedException("Login required");
        }
        if (!userContext.isManager() && !userContext.isAdmin()) {
            throw new LiveSpaceException.ForbiddenException("MANAGER or ADMIN role required");
        }
    }

    private Long resolveProfileId(UserContext userContext) {
        if (userContext.getUserKey() == null || userContext.getUserKey().isBlank()) {
            throw new LiveSpaceException.ForbiddenException("Missing user key");
        }
        return adminProfileService.resolveProfileId(
                userContext.getUserKey(),
                userContext.getUserName(),
                userContext.getRole()
        );
    }
}
