package com.zeroq.back.service.sensor.biz;

import auth.common.core.context.UserContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.service.sensor.vo.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class SensorBridgeClient {
    private static final String USER_ROLE_HEADER = "X-User-Role";
    private static final String USER_KEY_HEADER = "X-User-Key";
    private static final String USER_NAME_HEADER = "X-User-Name";

    private final RestClient sensorBridgeRestClient;
    private final ObjectMapper objectMapper;

    public List<SensorDeviceDTO> getSensors(Long placeId, UserContext userContext) {
        SensorEnvelope<List<SensorDeviceDTO>> envelope = executeGet(
                uriBuilder -> {
                    UriBuilder builder = uriBuilder.path("/api/zeroq/v1/sensor/devices");
                    if (placeId != null) {
                        builder = builder.queryParam("placeId", placeId);
                    }
                    return builder.build();
                },
                userContext,
                new ParameterizedTypeReference<>() {
                },
                "센서 목록 조회"
        );
        return envelope.getData() == null ? List.of() : envelope.getData();
    }

    public SensorDeviceDTO registerSensor(RegisterSensorDeviceRequest request, UserContext userContext) {
        SensorRegisterBridgeRequest bridgeRequest = new SensorRegisterBridgeRequest();
        bridgeRequest.setSensorId(request.getSensorId());
        bridgeRequest.setMacAddress(request.getMacAddress());
        bridgeRequest.setModel(request.getModel());
        bridgeRequest.setFirmwareVersion(request.getFirmwareVersion());
        bridgeRequest.setType(normalizeEnumText(request.getType()));
        bridgeRequest.setProtocol(normalizeEnumText(request.getProtocol()));
        bridgeRequest.setPlaceId(request.getPlaceId());
        bridgeRequest.setPositionCode(request.getPositionCode());
        bridgeRequest.setOccupancyThresholdCm(request.getOccupancyThresholdCm());
        bridgeRequest.setCalibrationOffsetCm(request.getCalibrationOffsetCm());
        bridgeRequest.setMetadataJson(request.getMetadataJson());

        return executePost(
                uriBuilder -> uriBuilder.path("/api/zeroq/v1/sensor/devices").build(),
                userContext,
                bridgeRequest,
                new ParameterizedTypeReference<SensorEnvelope<SensorDeviceDTO>>() {
                },
                "센서 등록"
        ).getData();
    }

    public SensorDeviceDTO installSensor(String sensorId, InstallSensorDeviceRequest request, UserContext userContext) {
        SensorInstallBridgeRequest bridgeRequest = new SensorInstallBridgeRequest();
        bridgeRequest.setPlaceId(request.getPlaceId());
        bridgeRequest.setPositionCode(request.getPositionCode());
        bridgeRequest.setOccupancyThresholdCm(request.getOccupancyThresholdCm());
        bridgeRequest.setCalibrationOffsetCm(request.getCalibrationOffsetCm());

        return executePut(
                uriBuilder -> uriBuilder.path("/api/zeroq/v1/sensor/devices/{sensorId}/install").build(sensorId),
                userContext,
                bridgeRequest,
                new ParameterizedTypeReference<SensorEnvelope<SensorDeviceDTO>>() {
                },
                "센서 설치"
        ).getData();
    }

    public SensorDeviceDTO updateSensorStatus(String sensorId, UpdateSensorDeviceStatusRequest request, UserContext userContext) {
        SensorStatusBridgeRequest bridgeRequest = new SensorStatusBridgeRequest();
        bridgeRequest.setStatus(normalizeEnumText(request.getStatus()));

        return executePatch(
                uriBuilder -> uriBuilder.path("/api/zeroq/v1/sensor/devices/{sensorId}/status").build(sensorId),
                userContext,
                bridgeRequest,
                new ParameterizedTypeReference<SensorEnvelope<SensorDeviceDTO>>() {
                },
                "센서 상태 변경"
        ).getData();
    }

    public SensorCommandDTO createCommand(
            CreateSensorCommandRequest request,
            String requestedBy,
            UserContext userContext
    ) {
        SensorCreateCommandBridgeRequest bridgeRequest = new SensorCreateCommandBridgeRequest();
        bridgeRequest.setSensorId(request.getSensorId());
        bridgeRequest.setCommandType(normalizeEnumText(request.getCommandType()));
        bridgeRequest.setCommandPayload(request.getCommandPayload());
        bridgeRequest.setRequestedBy(requestedBy);

        return executePost(
                uriBuilder -> uriBuilder.path("/api/zeroq/v1/sensor/commands").build(),
                userContext,
                bridgeRequest,
                new ParameterizedTypeReference<SensorEnvelope<SensorCommandDTO>>() {
                },
                "센서 명령 생성"
        ).getData();
    }

    public SensorPlaceSnapshotDTO getManagerSnapshot(Long placeId, boolean recalculate, UserContext userContext) {
        return executeGet(
                uriBuilder -> uriBuilder
                        .path("/api/zeroq/v1/sensor/monitoring/places/{placeId}/snapshot")
                        .queryParam("recalculate", recalculate)
                        .build(placeId),
                userContext,
                new ParameterizedTypeReference<SensorEnvelope<SensorPlaceSnapshotDTO>>() {
                },
                "관리자 스냅샷 조회"
        ).getData();
    }

    public SensorPlaceSnapshotDTO getUserSnapshot(Long placeId, boolean recalculate, UserContext userContext) {
        return executeGet(
                uriBuilder -> uriBuilder
                        .path("/api/zeroq/v1/sensor/monitoring/places/{placeId}/snapshot/public")
                        .queryParam("recalculate", recalculate)
                        .build(placeId),
                userContext,
                new ParameterizedTypeReference<SensorEnvelope<SensorPlaceSnapshotDTO>>() {
                },
                "사용자 스냅샷 조회"
        ).getData();
    }

    public List<SensorRecentTelemetryDTO> getRecentTelemetry(Long placeId, int limit, UserContext userContext) {
        SensorEnvelope<List<SensorRecentTelemetryDTO>> envelope = executeGet(
                uriBuilder -> uriBuilder
                        .path("/api/zeroq/v1/sensor/monitoring/places/{placeId}/telemetry/recent")
                        .queryParam("limit", limit)
                        .build(placeId),
                userContext,
                new ParameterizedTypeReference<>() {
                },
                "최근 텔레메트리 조회"
        );
        return envelope.getData() == null ? List.of() : envelope.getData();
    }

    private <T> SensorEnvelope<T> executeGet(
            Function<UriBuilder, URI> uriFunction,
            UserContext userContext,
            ParameterizedTypeReference<SensorEnvelope<T>> typeReference,
            String operation
    ) {
        try {
            SensorEnvelope<T> envelope = sensorBridgeRestClient.get()
                    .uri(uriFunction)
                    .headers(headers -> applyUserHeaders(headers, userContext))
                    .retrieve()
                    .body(typeReference);

            return validateEnvelope(envelope, operation);
        } catch (RestClientResponseException ex) {
            throw translateRemoteException(operation, ex);
        } catch (RestClientException ex) {
            throw new LiveSpaceException("SENSOR_BRIDGE_NETWORK_ERROR", operation + " 실패: " + ex.getMessage(), 502);
        }
    }

    private <T> SensorEnvelope<T> executePost(
            Function<UriBuilder, URI> uriFunction,
            UserContext userContext,
            Object body,
            ParameterizedTypeReference<SensorEnvelope<T>> typeReference,
            String operation
    ) {
        try {
            SensorEnvelope<T> envelope = sensorBridgeRestClient.post()
                    .uri(uriFunction)
                    .headers(headers -> applyUserHeaders(headers, userContext))
                    .body(body)
                    .retrieve()
                    .body(typeReference);

            return validateEnvelope(envelope, operation);
        } catch (RestClientResponseException ex) {
            throw translateRemoteException(operation, ex);
        } catch (RestClientException ex) {
            throw new LiveSpaceException("SENSOR_BRIDGE_NETWORK_ERROR", operation + " 실패: " + ex.getMessage(), 502);
        }
    }

    private <T> SensorEnvelope<T> executePut(
            Function<UriBuilder, URI> uriFunction,
            UserContext userContext,
            Object body,
            ParameterizedTypeReference<SensorEnvelope<T>> typeReference,
            String operation
    ) {
        try {
            SensorEnvelope<T> envelope = sensorBridgeRestClient.put()
                    .uri(uriFunction)
                    .headers(headers -> applyUserHeaders(headers, userContext))
                    .body(body)
                    .retrieve()
                    .body(typeReference);

            return validateEnvelope(envelope, operation);
        } catch (RestClientResponseException ex) {
            throw translateRemoteException(operation, ex);
        } catch (RestClientException ex) {
            throw new LiveSpaceException("SENSOR_BRIDGE_NETWORK_ERROR", operation + " 실패: " + ex.getMessage(), 502);
        }
    }

    private <T> SensorEnvelope<T> executePatch(
            Function<UriBuilder, URI> uriFunction,
            UserContext userContext,
            Object body,
            ParameterizedTypeReference<SensorEnvelope<T>> typeReference,
            String operation
    ) {
        try {
            SensorEnvelope<T> envelope = sensorBridgeRestClient.patch()
                    .uri(uriFunction)
                    .headers(headers -> applyUserHeaders(headers, userContext))
                    .body(body)
                    .retrieve()
                    .body(typeReference);

            return validateEnvelope(envelope, operation);
        } catch (RestClientResponseException ex) {
            throw translateRemoteException(operation, ex);
        } catch (RestClientException ex) {
            throw new LiveSpaceException("SENSOR_BRIDGE_NETWORK_ERROR", operation + " 실패: " + ex.getMessage(), 502);
        }
    }

    private <T> SensorEnvelope<T> validateEnvelope(SensorEnvelope<T> envelope, String operation) {
        if (envelope == null) {
            throw new LiveSpaceException("SENSOR_BRIDGE_EMPTY_RESPONSE", operation + " 응답이 비어 있습니다", 502);
        }
        if (!Boolean.TRUE.equals(envelope.getSuccess())) {
            String message = StringUtils.hasText(envelope.getMessage())
                    ? envelope.getMessage()
                    : operation + " 요청이 실패했습니다";
            throw new LiveSpaceException("SENSOR_BRIDGE_REMOTE_REJECTED", message, 502);
        }
        return envelope;
    }

    private RuntimeException translateRemoteException(String operation, RestClientResponseException ex) {
        int status = ex.getStatusCode().value();
        String message = extractRemoteMessage(ex.getResponseBodyAsString(), ex.getStatusText());
        String resolvedMessage = operation + " 실패: " + message;

        if (status == 401) {
            return new LiveSpaceException.UnauthorizedException(resolvedMessage);
        }
        if (status == 403) {
            return new LiveSpaceException.ForbiddenException(resolvedMessage);
        }
        if (status == 404) {
            return new LiveSpaceException("SENSOR_BRIDGE_NOT_FOUND", resolvedMessage, 404);
        }
        if (status == 409) {
            return new LiveSpaceException.ConflictException(resolvedMessage);
        }
        if (status >= 400 && status < 500) {
            return new LiveSpaceException.ValidationException(resolvedMessage);
        }
        return new LiveSpaceException("SENSOR_BRIDGE_REMOTE_ERROR", resolvedMessage, 502);
    }

    private String extractRemoteMessage(String responseBody, String fallback) {
        if (!StringUtils.hasText(responseBody)) {
            return fallback;
        }

        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode message = root.get("message");
            if (message != null && message.isTextual() && StringUtils.hasText(message.asText())) {
                return message.asText();
            }
        } catch (Exception ignored) {
            return responseBody;
        }

        return fallback;
    }

    private void applyUserHeaders(HttpHeaders headers, UserContext userContext) {
        if (userContext == null) {
            return;
        }

        if (StringUtils.hasText(userContext.getRole())) {
            headers.set(USER_ROLE_HEADER, normalizeRole(userContext.getRole()));
        }
        if (StringUtils.hasText(userContext.getUserKey())) {
            headers.set(USER_KEY_HEADER, userContext.getUserKey());
        }
        if (StringUtils.hasText(userContext.getUserName())) {
            headers.set(USER_NAME_HEADER, userContext.getUserName());
        }
    }

    private String normalizeEnumText(String rawValue) {
        if (!StringUtils.hasText(rawValue)) {
            return rawValue;
        }
        return rawValue.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeRole(String rawRole) {
        if (!StringUtils.hasText(rawRole)) {
            return rawRole;
        }

        String normalized = rawRole.trim().toUpperCase(Locale.ROOT);
        if (normalized.startsWith("ROLE_")) {
            return normalized.substring(5);
        }
        return normalized;
    }

    @Getter
    @Setter
    private static class SensorEnvelope<T> {
        private Boolean success;
        private Object code;
        private String message;
        private T data;
    }

    @Getter
    @Setter
    private static class SensorRegisterBridgeRequest {
        private String sensorId;
        private String macAddress;
        private String model;
        private String firmwareVersion;
        private String type;
        private String protocol;
        private Long placeId;
        private String positionCode;
        private Double occupancyThresholdCm;
        private Double calibrationOffsetCm;
        private String metadataJson;
    }

    @Getter
    @Setter
    private static class SensorInstallBridgeRequest {
        private Long placeId;
        private String positionCode;
        private Double occupancyThresholdCm;
        private Double calibrationOffsetCm;
    }

    @Getter
    @Setter
    private static class SensorStatusBridgeRequest {
        private String status;
    }

    @Getter
    @Setter
    private static class SensorCreateCommandBridgeRequest {
        private String sensorId;
        private String commandType;
        private String commandPayload;
        private String requestedBy;
    }
}
