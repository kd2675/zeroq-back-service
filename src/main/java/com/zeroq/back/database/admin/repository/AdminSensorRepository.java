package com.zeroq.back.database.admin.repository;

import com.zeroq.back.database.admin.entity.AdminSensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AdminSensorRepository extends JpaRepository<AdminSensor, Long> {
    Optional<AdminSensor> findBySensorId(String sensorId);

    Optional<AdminSensor> findByMacAddress(String macAddress);

    @Query("""
            select sensor
            from AdminSensor sensor
            join AdminGateway gateway on gateway.gatewayId = sensor.gatewayId
            where gateway.spaceId = :spaceId
            """)
    List<AdminSensor> findAllBySpaceId(@Param("spaceId") Long spaceId);

    List<AdminSensor> findAllByGatewayId(String gatewayId);
}
