package com.zeroq.back.database.pub.repository;

import com.zeroq.back.database.pub.entity.LowBatteryAlert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LowBatteryAlertRepository extends JpaRepository<LowBatteryAlert, Long> {
    Page<LowBatteryAlert> findBySensorIdOrderByCreateDateDesc(Long sensorId, Pageable pageable);

    @Query("SELECT la FROM LowBatteryAlert la WHERE la.acknowledged = false ORDER BY la.createDate DESC")
    Page<LowBatteryAlert> findUnacknowledgedAlerts(Pageable pageable);

    @Query("SELECT COUNT(la) FROM LowBatteryAlert la WHERE la.sensor.id = :sensorId AND la.acknowledged = false")
    long countUnacknowledgedAlertsBySensor(@Param("sensorId") Long sensorId);
}
