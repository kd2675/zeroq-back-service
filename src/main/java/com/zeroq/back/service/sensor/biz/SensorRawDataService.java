package com.zeroq.back.service.sensor.biz;

import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.sensor.entity.SensorCommand;
import com.zeroq.back.database.sensor.entity.SensorCommandStatus;
import com.zeroq.back.database.sensor.entity.SensorCommandType;
import com.zeroq.back.database.sensor.entity.SensorTelemetry;
import com.zeroq.back.database.sensor.repository.SensorCommandRepository;
import com.zeroq.back.database.sensor.repository.SensorHeartbeatRepository;
import com.zeroq.back.database.sensor.repository.SensorTelemetryRepository;
import com.zeroq.back.service.sensor.vo.CreateSensorCommandRequest;
import com.zeroq.back.service.sensor.vo.SensorCommandDTO;
import com.zeroq.back.service.sensor.vo.SensorRecentTelemetryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, transactionManager = "sensorTransactionManager")
public class SensorRawDataService {
    private final SensorTelemetryRepository sensorTelemetryRepository;
    private final SensorCommandRepository sensorCommandRepository;
    private final SensorHeartbeatRepository sensorHeartbeatRepository;

    public List<SensorRecentTelemetryDTO> getRecentTelemetryBySensorId(String sensorId, int limit) {
        int cappedLimit = Math.max(1, Math.min(limit, 200));
        return sensorTelemetryRepository.findTop200BySensorIdOrderByMeasuredAtDesc(sensorId).stream()
                .limit(cappedLimit)
                .map(this::toTelemetryDto)
                .toList();
    }

    @Transactional(transactionManager = "sensorTransactionManager")
    public SensorCommandDTO createCommand(CreateSensorCommandRequest request, String requestedBy) {
        SensorCommand command = SensorCommand.builder()
                .sensorId(request.getSensorId().trim())
                .commandType(parseEnum(SensorCommandType.class, request.getCommandType(), "sensor command type"))
                .status(SensorCommandStatus.PENDING)
                .commandPayload(request.getCommandPayload())
                .requestedBy(requestedBy)
                .requestedAt(LocalDateTime.now())
                .build();
        return toCommandDto(sensorCommandRepository.save(command));
    }

    @Transactional(transactionManager = "sensorTransactionManager")
    public void deleteSensor(String sensorId) {
        sensorCommandRepository.deleteAllBySensorId(sensorId);
        sensorTelemetryRepository.deleteAllBySensorId(sensorId);
        sensorHeartbeatRepository.deleteAllBySensorId(sensorId);
    }

    private SensorCommandDTO toCommandDto(SensorCommand command) {
        SensorCommandDTO dto = new SensorCommandDTO();
        dto.setId(command.getId());
        dto.setSensorId(command.getSensorId());
        dto.setCommandType(command.getCommandType().name());
        dto.setStatus(command.getStatus().name());
        dto.setCommandPayload(command.getCommandPayload());
        dto.setRequestedBy(command.getRequestedBy());
        dto.setRequestedAt(command.getRequestedAt());
        dto.setSentAt(command.getSentAt());
        dto.setAcknowledgedAt(command.getAcknowledgedAt());
        dto.setFailureReason(command.getFailureReason());
        dto.setAckPayload(command.getAckPayload());
        return dto;
    }

    private SensorRecentTelemetryDTO toTelemetryDto(SensorTelemetry telemetry) {
        SensorRecentTelemetryDTO dto = new SensorRecentTelemetryDTO();
        dto.setTelemetryId(telemetry.getId());
        dto.setSensorId(telemetry.getSensorId());
        dto.setDistanceCm(telemetry.getDistanceCm());
        dto.setOccupied(telemetry.isOccupied());
        dto.setPadLeftValue(telemetry.getPadLeftValue());
        dto.setPadRightValue(telemetry.getPadRightValue());
        dto.setQualityStatus(telemetry.getQualityStatus().name());
        dto.setMeasuredAt(telemetry.getMeasuredAt());
        dto.setReceivedAt(telemetry.getReceivedAt());
        dto.setBatteryPercent(telemetry.getBatteryPercent());
        dto.setConfidence(telemetry.getConfidence());
        return dto;
    }

    private <E extends Enum<E>> E parseEnum(Class<E> enumType, String rawValue, String label) {
        try {
            return Enum.valueOf(enumType, normalizeEnumText(rawValue));
        } catch (IllegalArgumentException ex) {
            throw new LiveSpaceException.ValidationException("Invalid " + label + ": " + rawValue);
        }
    }

    private String normalizeEnumText(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new LiveSpaceException.ValidationException("Enum value is required");
        }
        return rawValue.trim().toUpperCase(Locale.ROOT);
    }
}
