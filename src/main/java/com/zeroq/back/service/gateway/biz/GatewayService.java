package com.zeroq.back.service.gateway.biz;

import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.admin.entity.AdminGateway;
import com.zeroq.back.database.admin.entity.AdminSpace;
import com.zeroq.back.database.admin.repository.AdminGatewayRepository;
import com.zeroq.back.service.gateway.vo.CreateGatewayRequest;
import com.zeroq.back.service.gateway.vo.GatewayRegistryResponse;
import com.zeroq.back.service.space.biz.SpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GatewayService {
    private final AdminGatewayRepository adminGatewayRepository;
    private final SpaceService spaceService;

    @Transactional(transactionManager = "adminTransactionManager")
    public GatewayRegistryResponse createGateway(
            CreateGatewayRequest request,
            Long requesterProfileId,
            boolean adminView
    ) {
        String gatewayId = normalizeRequiredText(request.getGatewayId(), "gatewayId");
        if (adminGatewayRepository.existsByGatewayId(gatewayId)) {
            throw new LiveSpaceException.ConflictException("이미 등록된 gatewayId 입니다: " + gatewayId);
        }

        AdminSpace ownedSpace = spaceService.getOwnedSpace(request.getSpaceId(), requesterProfileId, adminView);

        AdminGateway gateway = AdminGateway.builder()
                .gatewayId(gatewayId)
                .spaceId(ownedSpace.getId())
                .gatewayName(normalizeRequiredText(request.getGatewayName(), "gatewayName"))
                .gatewayRole(defaultText(request.getGatewayRole(), "EDGE"))
                .regionCode(normalizeOptionalText(request.getRegionCode()))
                .locationLabel(normalizeOptionalText(request.getLocationLabel()))
                .ipAddress(normalizeOptionalText(request.getIpAddress()))
                .sensorCapacity(request.getSensorCapacity())
                .currentSensorLoad(0)
                .latencyMs(null)
                .packetLossPercent(null)
                .lastHeartbeatAt(null)
                .status("OFFLINE")
                .firmwareVersion(normalizeOptionalText(request.getFirmwareVersion()))
                .description(normalizeOptionalText(request.getDescription()))
                .linkedBridge(normalizeOptionalText(request.getLinkedBridge()))
                .build();

        AdminGateway savedGateway = adminGatewayRepository.save(gateway);
        return GatewayRegistryResponse.builder()
                .gatewayId(savedGateway.getGatewayId())
                .gatewayName(savedGateway.getGatewayName())
                .gatewayRole(savedGateway.getGatewayRole())
                .regionCode(savedGateway.getRegionCode())
                .spaceId(ownedSpace.getId())
                .spaceName(ownedSpace.getName())
                .locationLabel(savedGateway.getLocationLabel())
                .ipAddress(savedGateway.getIpAddress())
                .sensorCapacity(savedGateway.getSensorCapacity())
                .firmwareVersion(savedGateway.getFirmwareVersion())
                .description(savedGateway.getDescription())
                .linkedBridge(savedGateway.getLinkedBridge())
                .status(savedGateway.getStatus())
                .build();
    }

    private String normalizeRequiredText(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new LiveSpaceException.ValidationException(fieldName + "는 필수입니다");
        }
        return value.trim();
    }

    private String normalizeOptionalText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String defaultText(String value, String fallback) {
        return StringUtils.hasText(value) ? value.trim() : fallback;
    }
}
