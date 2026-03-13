package com.zeroq.back.service.sensor.biz;

import auth.common.core.context.UserContext;
import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.admin.entity.AdminGateway;
import com.zeroq.back.database.admin.entity.AdminSensor;
import com.zeroq.back.database.admin.entity.AdminSpace;
import com.zeroq.back.database.admin.repository.AdminGatewayRepository;
import com.zeroq.back.database.admin.repository.AdminSensorRepository;
import com.zeroq.back.service.sensor.vo.InstallSensorDeviceRequest;
import com.zeroq.back.service.sensor.vo.SensorDeviceDTO;
import com.zeroq.back.service.sensor.vo.SensorPublicSnapshotResponse;
import com.zeroq.back.service.sensor.vo.SensorRecentTelemetryDTO;
import com.zeroq.back.service.sensor.vo.SensorSpaceOverviewResponse;
import com.zeroq.back.service.space.biz.SpaceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SensorBridgeServiceTests {

    @Mock
    private SpaceService spaceService;

    @Mock
    private SensorRawDataService sensorRawDataService;

    @Mock
    private AdminSensorRepository adminSensorRepository;

    @Mock
    private AdminGatewayRepository adminGatewayRepository;

    @InjectMocks
    private SensorBridgeService sensorBridgeService;

    @Test
    void getManagerSpaceOverview_returnsComposedOverview_forManagerRole() {
        UserContext manager = UserContext.builder()
                .userKey("manager-1")
                .userName("manager")
                .role("MANAGER")
                .build();

        AdminSpace space = AdminSpace.builder()
                .id(10L)
                .name("Gangnam Branch")
                .build();

        AdminSensor sensor = AdminSensor.builder()
                .id(1L)
                .sensorId("S-001")
                .macAddress("AA:BB:CC:00:00:01")
                .model("ESP32")
                .type("OCCUPANCY_DETECTION")
                .protocol("MQTT")
                .status("ACTIVE")
                .gatewayId("GW-MANAGER-10")
                .build();

        AdminGateway gateway = AdminGateway.builder()
                .gatewayId("GW-MANAGER-10")
                .spaceId(10L)
                .status("ONLINE")
                .gatewayName("Gateway Manager 10")
                .build();

        SensorRecentTelemetryDTO telemetry = new SensorRecentTelemetryDTO();
        telemetry.setSensorId("S-001");
        telemetry.setOccupied(true);
        telemetry.setMeasuredAt(java.time.LocalDateTime.now());

        when(spaceService.getSpaceById(10L)).thenReturn(space);
        when(adminSensorRepository.findAllBySpaceId(10L)).thenReturn(List.of(sensor));
        when(adminGatewayRepository.findByGatewayId("GW-MANAGER-10")).thenReturn(java.util.Optional.of(gateway));
        when(sensorRawDataService.getRecentTelemetryBySensorId("S-001", 10)).thenReturn(List.of(telemetry));

        SensorSpaceOverviewResponse response = sensorBridgeService.getManagerSpaceOverview(10L, true, 999, manager);

        assertThat(response.getSpaceId()).isEqualTo(10L);
        assertThat(response.getSpaceName()).isEqualTo("Gangnam Branch");
        assertThat(response.getSnapshot().getOccupancyRate()).isEqualTo(100.0);
        assertThat(response.getSensors()).hasSize(1);
        assertThat(response.getRecentTelemetry()).hasSize(1);

        verify(sensorRawDataService).getRecentTelemetryBySensorId("S-001", 10);
    }

    @Test
    void getManagerSpaceOverview_throwsForbidden_forUserRole() {
        UserContext user = UserContext.builder()
                .userKey("user-1")
                .userName("user")
                .role("USER")
                .build();

        assertThatThrownBy(() -> sensorBridgeService.getManagerSpaceOverview(1L, false, 20, user))
                .isInstanceOf(LiveSpaceException.ForbiddenException.class);
    }

    @Test
    void getPublicSpaceSnapshot_returnsSnapshot_forAuthenticatedUser() {
        UserContext user = UserContext.builder()
                .userKey("user-2")
                .userName("visitor")
                .role("USER")
                .build();

        AdminSpace space = AdminSpace.builder()
                .id(3L)
                .name("Hongdae Branch")
                .build();

        AdminSensor sensor = AdminSensor.builder()
                .id(1L)
                .sensorId("S-101")
                .macAddress("AA:BB:CC:00:00:02")
                .model("ESP32")
                .type("OCCUPANCY_DETECTION")
                .protocol("MQTT")
                .status("ACTIVE")
                .gatewayId("GW-PUBLIC-03")
                .build();

        AdminGateway gateway = AdminGateway.builder()
                .gatewayId("GW-PUBLIC-03")
                .spaceId(3L)
                .status("ONLINE")
                .gatewayName("Gateway Public 03")
                .build();

        SensorRecentTelemetryDTO telemetry = new SensorRecentTelemetryDTO();
        telemetry.setSensorId("S-101");
        telemetry.setOccupied(false);
        telemetry.setMeasuredAt(java.time.LocalDateTime.now());

        when(spaceService.getSpaceById(3L)).thenReturn(space);
        when(adminSensorRepository.findAllBySpaceId(3L)).thenReturn(List.of(sensor));
        when(adminGatewayRepository.findByGatewayId("GW-PUBLIC-03")).thenReturn(java.util.Optional.of(gateway));
        when(sensorRawDataService.getRecentTelemetryBySensorId("S-101", 10)).thenReturn(List.of(telemetry));

        SensorPublicSnapshotResponse response = sensorBridgeService.getPublicSpaceSnapshot(3L, false, user);

        assertThat(response.getSpaceId()).isEqualTo(3L);
        assertThat(response.getSpaceName()).isEqualTo("Hongdae Branch");
        assertThat(response.getSnapshot().getOccupancyRate()).isEqualTo(0.0);
    }

    @Test
    void getPublicSpaceSnapshot_throwsUnauthorized_whenUserContextIsMissing() {
        assertThatThrownBy(() -> sensorBridgeService.getPublicSpaceSnapshot(1L, false, null))
                .isInstanceOf(LiveSpaceException.UnauthorizedException.class);
    }

    @Test
    void installSensor_assignsGatewayId_andBindsUnassignedGatewayToSpace() {
        UserContext manager = UserContext.builder()
                .userKey("manager-1")
                .userName("manager")
                .role("MANAGER")
                .build();

        AdminSensor sensor = AdminSensor.builder()
                .id(10L)
                .sensorId("S-010")
                .macAddress("AA:BB:CC:00:00:10")
                .model("ESP32")
                .type("OCCUPANCY_DETECTION")
                .protocol("MQTT")
                .status("REGISTERED")
                .build();

        AdminGateway gateway = AdminGateway.builder()
                .id(20L)
                .gatewayId("GW-SEOUL-10")
                .spaceId(null)
                .status("REGISTERED")
                .build();

        InstallSensorDeviceRequest request = new InstallSensorDeviceRequest();
        request.setPlaceId(33L);
        request.setGatewayId("GW-SEOUL-10");

        when(adminSensorRepository.findBySensorId("S-010")).thenReturn(java.util.Optional.of(sensor));
        when(adminSensorRepository.save(any(AdminSensor.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(adminGatewayRepository.findByGatewayId("GW-SEOUL-10")).thenReturn(java.util.Optional.of(gateway));
        when(adminGatewayRepository.save(any(AdminGateway.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SensorDeviceDTO response = sensorBridgeService.installSensor("S-010", request, manager);

        assertThat(response.getGatewayId()).isEqualTo("GW-SEOUL-10");
        assertThat(response.getPlaceId()).isEqualTo(33L);
        assertThat(gateway.getSpaceId()).isEqualTo(33L);
    }

    @Test
    void registerSensor_rejectsDirectSpaceAssignment_withoutGateway() {
        UserContext manager = UserContext.builder()
                .userKey("manager-1")
                .userName("manager")
                .role("MANAGER")
                .build();

        com.zeroq.back.service.sensor.vo.RegisterSensorDeviceRequest request =
                new com.zeroq.back.service.sensor.vo.RegisterSensorDeviceRequest();
        request.setSensorId("S-999");
        request.setMacAddress("AA:BB:CC:99:99:99");
        request.setModel("ESP32");
        request.setType("OCCUPANCY_DETECTION");
        request.setProtocol("MQTT");
        request.setPlaceId(77L);

        assertThatThrownBy(() -> sensorBridgeService.registerSensor(request, manager))
                .isInstanceOf(LiveSpaceException.ValidationException.class)
                .hasMessageContaining("without gatewayId");
    }

    @Test
    void deleteSensor_removesRawDataAndAdminRegistry_forManager() {
        UserContext manager = UserContext.builder()
                .userKey("manager-1")
                .userName("manager")
                .role("MANAGER")
                .build();

        AdminSensor sensor = AdminSensor.builder()
                .id(99L)
                .sensorId("S-DEL-99")
                .macAddress("AA:BB:CC:99:00:99")
                .model("ESP32")
                .type("OCCUPANCY_DETECTION")
                .protocol("MQTT")
                .status("ACTIVE")
                .gatewayId("GW-DEL-01")
                .build();

        when(adminSensorRepository.findBySensorId("S-DEL-99")).thenReturn(java.util.Optional.of(sensor));

        sensorBridgeService.deleteSensor("S-DEL-99", manager);

        verify(sensorRawDataService).deleteSensor("S-DEL-99");
        verify(adminSensorRepository).delete(sensor);
    }

    @Test
    void deleteSensor_rejectsUserRole() {
        UserContext user = UserContext.builder()
                .userKey("user-1")
                .userName("user")
                .role("USER")
                .build();

        assertThatThrownBy(() -> sensorBridgeService.deleteSensor("S-DEL-01", user))
                .isInstanceOf(LiveSpaceException.ForbiddenException.class);

        verify(sensorRawDataService, never()).deleteSensor(any());
    }
}
