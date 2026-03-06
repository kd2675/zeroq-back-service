package com.zeroq.back.service.sensor.act;

import auth.common.core.context.UserContext;
import com.zeroq.back.service.sensor.biz.SensorBridgeService;
import com.zeroq.back.service.sensor.vo.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import web.common.core.response.base.dto.ResponseDataDTO;

import java.util.List;

@RestController
@RequestMapping("/api/zeroq/v1/space-sensors")
@RequiredArgsConstructor
public class SensorBridgeController {
    private final SensorBridgeService sensorBridgeService;

    @GetMapping("/spaces/{spaceId}/overview")
    public ResponseDataDTO<SensorSpaceOverviewResponse> getSpaceOverview(
            @PathVariable Long spaceId,
            @RequestParam(defaultValue = "false") boolean recalculate,
            @RequestParam(defaultValue = "30") int telemetryLimit,
            UserContext userContext
    ) {
        SensorSpaceOverviewResponse response = sensorBridgeService.getManagerSpaceOverview(
                spaceId,
                recalculate,
                telemetryLimit,
                userContext
        );
        return ResponseDataDTO.of(response, "공간 센서 오버뷰 조회 완료");
    }

    @GetMapping("/spaces/{spaceId}/snapshot")
    public ResponseDataDTO<SensorPublicSnapshotResponse> getPublicSnapshot(
            @PathVariable Long spaceId,
            @RequestParam(defaultValue = "false") boolean recalculate,
            UserContext userContext
    ) {
        SensorPublicSnapshotResponse response = sensorBridgeService.getPublicSpaceSnapshot(
                spaceId,
                recalculate,
                userContext
        );
        return ResponseDataDTO.of(response, "공간 센서 스냅샷 조회 완료");
    }

    @GetMapping("/devices")
    public ResponseDataDTO<List<SensorDeviceDTO>> getDevices(
            @RequestParam(required = false) Long placeId,
            UserContext userContext
    ) {
        return ResponseDataDTO.of(sensorBridgeService.getDevices(placeId, userContext), "센서 목록 조회 완료");
    }

    @PostMapping("/devices")
    public ResponseDataDTO<SensorDeviceDTO> registerSensor(
            @Valid @RequestBody RegisterSensorDeviceRequest request,
            UserContext userContext
    ) {
        return ResponseDataDTO.of(sensorBridgeService.registerSensor(request, userContext), "센서 등록 완료");
    }

    @PutMapping("/devices/{sensorId}/install")
    public ResponseDataDTO<SensorDeviceDTO> installSensor(
            @PathVariable String sensorId,
            @Valid @RequestBody InstallSensorDeviceRequest request,
            UserContext userContext
    ) {
        return ResponseDataDTO.of(sensorBridgeService.installSensor(sensorId, request, userContext), "센서 설치 반영 완료");
    }

    @PatchMapping("/devices/{sensorId}/status")
    public ResponseDataDTO<SensorDeviceDTO> updateStatus(
            @PathVariable String sensorId,
            @Valid @RequestBody UpdateSensorDeviceStatusRequest request,
            UserContext userContext
    ) {
        return ResponseDataDTO.of(sensorBridgeService.updateSensorStatus(sensorId, request, userContext), "센서 상태 변경 완료");
    }

    @PostMapping("/commands")
    public ResponseDataDTO<SensorCommandDTO> createCommand(
            @Valid @RequestBody CreateSensorCommandRequest request,
            UserContext userContext
    ) {
        return ResponseDataDTO.of(sensorBridgeService.createCommand(request, userContext), "센서 명령 생성 완료");
    }
}
