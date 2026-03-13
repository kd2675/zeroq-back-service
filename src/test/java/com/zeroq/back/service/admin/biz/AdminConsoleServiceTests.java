package com.zeroq.back.service.admin.biz;

import auth.common.core.context.UserContext;
import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.admin.entity.AdminGateway;
import com.zeroq.back.database.admin.entity.AdminSpace;
import com.zeroq.back.database.admin.repository.AdminGatewayRepository;
import com.zeroq.back.database.sensor.entity.GatewayStatusSnapshot;
import com.zeroq.back.database.admin.entity.NotificationPreference;
import com.zeroq.back.database.admin.entity.UserPreference;
import com.zeroq.back.database.admin.repository.NotificationPreferenceRepository;
import com.zeroq.back.database.admin.repository.NotificationRepository;
import com.zeroq.back.database.admin.repository.UserPreferenceRepository;
import com.zeroq.back.database.pub.repository.ReviewRepository;
import com.zeroq.back.service.admin.vo.AdminConsoleSettingsResponse;
import com.zeroq.back.service.admin.vo.AdminWorkspaceResponse;
import com.zeroq.back.service.admin.vo.UpdateAdminConsoleSettingsRequest;
import com.zeroq.back.service.sensor.biz.GatewayRuntimeSnapshotService;
import com.zeroq.back.service.sensor.biz.SensorBridgeService;
import com.zeroq.back.service.sensor.vo.SensorDeviceDTO;
import com.zeroq.back.service.sensor.vo.SensorPlaceSnapshotDTO;
import com.zeroq.back.service.sensor.vo.SensorRecentTelemetryDTO;
import com.zeroq.back.service.sensor.vo.SensorSpaceOverviewResponse;
import com.zeroq.back.service.space.biz.SpaceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminConsoleServiceTests {

    @Mock
    private SpaceService spaceService;

    @Mock
    private SensorBridgeService sensorBridgeService;

    @Mock
    private GatewayRuntimeSnapshotService gatewayRuntimeSnapshotService;

    @Mock
    private AdminGatewayRepository adminGatewayRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationPreferenceRepository notificationPreferenceRepository;

    @Mock
    private UserPreferenceRepository userPreferenceRepository;

    @Mock
    private AdminProfileService adminProfileService;

    @InjectMocks
    private AdminConsoleService adminConsoleService;

    @Test
    void getWorkspace_returnsAggregatedConsolePayload_forManager() {
        UserContext manager = managerContext();
        AdminSpace space = AdminSpace.builder()
                .id(7L)
                .name("North Wing")
                .averageRating(4.6)
                .verified(true)
                .active(true)
                .build();
        Page<AdminSpace> page = new PageImpl<>(List.of(space), PageRequest.of(0, 24), 1);

        SensorPlaceSnapshotDTO snapshot = new SensorPlaceSnapshotDTO();
        snapshot.setPlaceId(7L);
        snapshot.setOccupiedCount(12);
        snapshot.setActiveSensorCount(2);
        snapshot.setOccupancyRate(60.0);
        snapshot.setCrowdLevel("MEDIUM");
        snapshot.setLastMeasuredAt(LocalDateTime.now().minusMinutes(2));

        SensorDeviceDTO activeSensor = new SensorDeviceDTO();
        activeSensor.setId(101L);
        activeSensor.setSensorId("SN-101");
        activeSensor.setModel("PIR-X");
        activeSensor.setType("OCCUPANCY_DETECTION");
        activeSensor.setProtocol("MQTT");
        activeSensor.setStatus("ACTIVE");
        activeSensor.setPlaceId(7L);
        activeSensor.setGatewayId("GW-SEOUL-07");
        activeSensor.setBatteryPercent(72.0);
        activeSensor.setLastHeartbeatAt(LocalDateTime.now().minusMinutes(1));

        SensorDeviceDTO offlineSensor = new SensorDeviceDTO();
        offlineSensor.setId(102L);
        offlineSensor.setSensorId("SN-102");
        offlineSensor.setModel("PIR-Y");
        offlineSensor.setType("OCCUPANCY_DETECTION");
        offlineSensor.setProtocol("MQTT");
        offlineSensor.setStatus("MAINTENANCE");
        offlineSensor.setPlaceId(7L);
        offlineSensor.setGatewayId("GW-SEOUL-07");
        offlineSensor.setBatteryPercent(12.0);
        offlineSensor.setLastHeartbeatAt(LocalDateTime.now().minusMinutes(12));

        SensorRecentTelemetryDTO telemetry = new SensorRecentTelemetryDTO();
        telemetry.setSensorId("SN-101");
        telemetry.setOccupied(true);
        telemetry.setMeasuredAt(LocalDateTime.now().minusMinutes(1));

        SensorSpaceOverviewResponse overview = SensorSpaceOverviewResponse.builder()
                .spaceId(7L)
                .spaceName("North Wing")
                .snapshot(snapshot)
                .sensors(List.of(activeSensor, offlineSensor))
                .recentTelemetry(List.of(telemetry))
                .build();

        AdminGateway gateway = AdminGateway.builder()
                .id(33L)
                .gatewayId("GW-SEOUL-07")
                .spaceId(7L)
                .status("ONLINE")
                .firmwareVersion("1.2.3")
                .linkedBridge("mqtt://bridge-seoul-07")
                .build();

        when(spaceService.getWorkspaceManagedSpaces(11L, false, PageRequest.of(0, 24))).thenReturn(page);
        when(adminProfileService.resolveProfileId("manager-1", "manager", "MANAGER")).thenReturn(11L);
        when(sensorBridgeService.getManagerSpaceOverview(7L, false, 12, manager)).thenReturn(overview);
        when(adminGatewayRepository.findAll()).thenReturn(List.of(gateway));
        when(gatewayRuntimeSnapshotService.getSnapshots(List.of("GW-SEOUL-07"))).thenReturn(Map.of(
                "GW-SEOUL-07",
                GatewayStatusSnapshot.builder()
                        .gatewayId("GW-SEOUL-07")
                        .status("ONLINE")
                        .currentSensorLoad(2)
                        .latencyMs(14)
                        .packetLossPercent(0.4)
                        .lastHeartbeatAt(LocalDateTime.now())
                        .build()
        ));
        when(reviewRepository.countReviewsBySpace(7L)).thenReturn(5L);
        when(notificationRepository.findByProfileIdOrderByCreateDateDesc(eq(11L), any(Pageable.class)))
                .thenReturn(Page.empty());

        AdminWorkspaceResponse response = adminConsoleService.getWorkspace(manager);

        assertThat(response.getSpaces()).hasSize(1);
        assertThat(response.getSensors()).hasSize(2);
        assertThat(response.getGateways()).hasSize(1);
        assertThat(response.getGateways().get(0).getGatewayId()).isEqualTo("GW-SEOUL-07");
        assertThat(response.getGateways().get(0).getStatus()).isEqualTo("Online");
        assertThat(response.getGateways().get(0).getLatencyMs()).isEqualTo(14);
        assertThat(response.getSensors()).extracting("gatewayId").containsOnly("GW-SEOUL-07");
        assertThat(response.getSpaces().get(0).getLowBatteryCount()).isEqualTo(1);
        assertThat(response.getSpaces().get(0).getOfflineCount()).isEqualTo(1);
        assertThat(response.getSummary().getOccupiedNow()).isEqualTo(12);
        assertThat(response.getSummary().getActiveSensors()).isEqualTo(1);
        assertThat(response.getSummary().getOfflineSensors()).isEqualTo(1);
        assertThat(response.getSummary().getOccupancyRate()).isEqualTo(60.0);
        assertThat(response.getAlerts())
                .extracting("type")
                .contains("Battery Watch", "Sensor Offline");
    }

    @Test
    void getWorkspace_doesNotSynthesizeGatewayOrTrend_whenRegistryAndTelemetryAreMissing() {
        UserContext manager = managerContext();
        AdminSpace space = AdminSpace.builder()
                .id(9L)
                .name("South Lab")
                .verified(true)
                .active(true)
                .build();
        Page<AdminSpace> page = new PageImpl<>(List.of(space), PageRequest.of(0, 24), 1);

        SensorPlaceSnapshotDTO snapshot = new SensorPlaceSnapshotDTO();
        snapshot.setPlaceId(9L);
        snapshot.setOccupiedCount(4);
        snapshot.setActiveSensorCount(1);
        snapshot.setOccupancyRate(33.0);
        snapshot.setCrowdLevel("LOW");

        SensorDeviceDTO sensor = new SensorDeviceDTO();
        sensor.setId(201L);
        sensor.setSensorId("SN-201");
        sensor.setModel("PIR-Z");
        sensor.setType("OCCUPANCY_DETECTION");
        sensor.setProtocol("MQTT");
        sensor.setStatus("ACTIVE");
        sensor.setPlaceId(9L);
        sensor.setBatteryPercent(51.0);

        SensorSpaceOverviewResponse overview = SensorSpaceOverviewResponse.builder()
                .spaceId(9L)
                .spaceName("South Lab")
                .snapshot(snapshot)
                .sensors(List.of(sensor))
                .recentTelemetry(List.of())
                .build();

        when(spaceService.getWorkspaceManagedSpaces(11L, false, PageRequest.of(0, 24))).thenReturn(page);
        when(adminProfileService.resolveProfileId("manager-1", "manager", "MANAGER")).thenReturn(11L);
        when(sensorBridgeService.getManagerSpaceOverview(9L, false, 12, manager)).thenReturn(overview);
        when(adminGatewayRepository.findAll()).thenReturn(List.of());
        when(gatewayRuntimeSnapshotService.getSnapshots(List.of())).thenReturn(Map.of());
        when(reviewRepository.countReviewsBySpace(9L)).thenReturn(0L);
        when(notificationRepository.findByProfileIdOrderByCreateDateDesc(eq(11L), any(Pageable.class)))
                .thenReturn(Page.empty());

        AdminWorkspaceResponse response = adminConsoleService.getWorkspace(manager);

        assertThat(response.getGateways()).isEmpty();
        assertThat(response.getSensors()).hasSize(1);
        assertThat(response.getSensors().get(0).getGatewayId()).isNull();
        assertThat(response.getSpaces().get(0).getTrend()).isEmpty();
        assertThat(response.getSummary().getGatewayHealth()).isZero();
    }

    @Test
    void updateSettings_persistsPreferencesAndReturnsSavedState() {
        UserContext manager = managerContext();
        when(adminProfileService.resolveProfileId("manager-1", "manager", "MANAGER")).thenReturn(11L);

        NotificationPreference notificationPreference = NotificationPreference.builder()
                .profileId(11L)
                .allNotificationsEnabled(false)
                .occupancyNotificationsEnabled(false)
                .batteryNotificationsEnabled(false)
                .emailNotificationsEnabled(false)
                .pushNotificationsEnabled(false)
                .smsNotificationsEnabled(false)
                .build();
        List<UserPreference> storedPreferences = new ArrayList<>();

        when(notificationPreferenceRepository.findByProfileId(11L))
                .thenReturn(Optional.of(notificationPreference));
        when(notificationPreferenceRepository.save(any(NotificationPreference.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(userPreferenceRepository.findByProfileId(11L))
                .thenAnswer(invocation -> new ArrayList<>(storedPreferences));
        when(userPreferenceRepository.findByProfileIdAndPreferenceKey(eq(11L), anyString()))
                .thenAnswer(invocation -> storedPreferences.stream()
                        .filter(preference -> preference.getPreferenceKey().equals(invocation.getArgument(1)))
                        .findFirst());
        when(userPreferenceRepository.save(any(UserPreference.class)))
                .thenAnswer(invocation -> {
                    UserPreference preference = invocation.getArgument(0);
                    storedPreferences.removeIf(item -> item.getPreferenceKey().equals(preference.getPreferenceKey()));
                    storedPreferences.add(preference);
                    return preference;
                });

        UpdateAdminConsoleSettingsRequest request = new UpdateAdminConsoleSettingsRequest(
                180,
                80,
                "1 Year",
                "60 Days",
                "2 Years",
                true,
                true,
                true,
                true,
                false,
                true
        );

        AdminConsoleSettingsResponse response = adminConsoleService.updateSettings(request, manager);

        assertThat(response.getOvercapacityLimit()).isEqualTo(180);
        assertThat(response.getWarningBufferPercent()).isEqualTo(80);
        assertThat(response.getSensorRawDataRetention()).isEqualTo("1 Year");
        assertThat(response.getSystemErrorRetention()).isEqualTo("60 Days");
        assertThat(response.getAlertHistoryRetention()).isEqualTo("2 Years");
        assertThat(response.isAllNotificationsEnabled()).isTrue();
        assertThat(response.isEmailNotificationsEnabled()).isTrue();
        assertThat(response.isPushNotificationsEnabled()).isFalse();
        assertThat(response.isSmsNotificationsEnabled()).isTrue();

        verify(notificationPreferenceRepository).save(notificationPreference);
        verify(userPreferenceRepository, times(5)).save(any(UserPreference.class));
    }

    @Test
    void getWorkspace_throwsForbidden_forUserRole() {
        UserContext user = UserContext.builder()
                .userKey("user-1")
                .userName("user")
                .role("USER")
                .build();

        assertThatThrownBy(() -> adminConsoleService.getWorkspace(user))
                .isInstanceOf(LiveSpaceException.ForbiddenException.class);
    }

    private UserContext managerContext() {
        return UserContext.builder()
                .userKey("manager-1")
                .userName("manager")
                .role("MANAGER")
                .build();
    }
}
