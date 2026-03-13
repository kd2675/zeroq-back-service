package com.zeroq.back.database.sensor.repository;

import com.zeroq.back.database.sensor.entity.GatewayStatusSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface GatewayStatusSnapshotRepository extends JpaRepository<GatewayStatusSnapshot, Long> {
    List<GatewayStatusSnapshot> findByGatewayIdIn(Collection<String> gatewayIds);
}
