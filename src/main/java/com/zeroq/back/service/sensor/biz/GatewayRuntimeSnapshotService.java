package com.zeroq.back.service.sensor.biz;

import com.zeroq.back.database.sensor.entity.GatewayStatusSnapshot;
import com.zeroq.back.database.sensor.repository.GatewayStatusSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GatewayRuntimeSnapshotService {
    private final GatewayStatusSnapshotRepository gatewayStatusSnapshotRepository;

    public Map<String, GatewayStatusSnapshot> getSnapshots(Collection<String> gatewayIds) {
        if (gatewayIds == null || gatewayIds.isEmpty()) {
            return Map.of();
        }
        return gatewayStatusSnapshotRepository.findByGatewayIdIn(gatewayIds).stream()
                .collect(Collectors.toMap(GatewayStatusSnapshot::getGatewayId, Function.identity(), (left, right) -> right));
    }
}
