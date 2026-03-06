package com.zeroq.back.service.sensor.biz;

import auth.common.core.context.UserContext;
import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.pub.entity.Space;
import com.zeroq.back.service.sensor.vo.SensorDeviceDTO;
import com.zeroq.back.service.sensor.vo.SensorPlaceSnapshotDTO;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SensorBridgeServiceTests {

    @Mock
    private SpaceService spaceService;

    @Mock
    private SensorBridgeClient sensorBridgeClient;

    @InjectMocks
    private SensorBridgeService sensorBridgeService;

    @Test
    void getManagerSpaceOverview_returnsComposedOverview_forManagerRole() {
        UserContext manager = UserContext.builder()
                .userKey("manager-1")
                .userName("manager")
                .role("MANAGER")
                .build();

        Space space = Space.builder()
                .id(10L)
                .name("Gangnam Branch")
                .capacity(120)
                .build();

        SensorPlaceSnapshotDTO snapshot = new SensorPlaceSnapshotDTO();
        snapshot.setPlaceId(10L);
        snapshot.setOccupancyRate(42.5);

        SensorDeviceDTO sensor = new SensorDeviceDTO();
        sensor.setSensorId("S-001");

        SensorRecentTelemetryDTO telemetry = new SensorRecentTelemetryDTO();
        telemetry.setSensorId("S-001");

        when(spaceService.getSpaceById(10L)).thenReturn(space);
        when(sensorBridgeClient.getManagerSnapshot(10L, true, manager)).thenReturn(snapshot);
        when(sensorBridgeClient.getSensors(10L, manager)).thenReturn(List.of(sensor));
        when(sensorBridgeClient.getRecentTelemetry(10L, 200, manager)).thenReturn(List.of(telemetry));

        SensorSpaceOverviewResponse response = sensorBridgeService.getManagerSpaceOverview(10L, true, 999, manager);

        assertThat(response.getSpaceId()).isEqualTo(10L);
        assertThat(response.getSpaceName()).isEqualTo("Gangnam Branch");
        assertThat(response.getSnapshot().getOccupancyRate()).isEqualTo(42.5);
        assertThat(response.getSensors()).hasSize(1);
        assertThat(response.getRecentTelemetry()).hasSize(1);

        verify(sensorBridgeClient).getRecentTelemetry(10L, 200, manager);
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

        Space space = Space.builder()
                .id(3L)
                .name("Hongdae Branch")
                .capacity(90)
                .build();

        SensorPlaceSnapshotDTO snapshot = new SensorPlaceSnapshotDTO();
        snapshot.setPlaceId(3L);
        snapshot.setOccupancyRate(18.0);

        when(spaceService.getSpaceById(3L)).thenReturn(space);
        when(sensorBridgeClient.getUserSnapshot(3L, false, user)).thenReturn(snapshot);

        SensorPublicSnapshotResponse response = sensorBridgeService.getPublicSpaceSnapshot(3L, false, user);

        assertThat(response.getSpaceId()).isEqualTo(3L);
        assertThat(response.getSpaceName()).isEqualTo("Hongdae Branch");
        assertThat(response.getSnapshot().getOccupancyRate()).isEqualTo(18.0);
    }

    @Test
    void getPublicSpaceSnapshot_throwsUnauthorized_whenUserContextIsMissing() {
        assertThatThrownBy(() -> sensorBridgeService.getPublicSpaceSnapshot(1L, false, null))
                .isInstanceOf(LiveSpaceException.UnauthorizedException.class);
    }
}
