package com.zeroq.back.database.admin.repository;

import com.zeroq.back.database.admin.entity.AdminGateway;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdminGatewayRepository extends JpaRepository<AdminGateway, Long> {
    Optional<AdminGateway> findByGatewayId(String gatewayId);

    List<AdminGateway> findAllBySpaceId(Long spaceId);

    boolean existsByGatewayId(String gatewayId);
}
