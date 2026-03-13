package com.zeroq.back.database.sensor.repository;

import com.zeroq.back.database.sensor.entity.SensorHeartbeat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorHeartbeatRepository extends JpaRepository<SensorHeartbeat, Long> {
    void deleteAllBySensorId(String sensorId);
}
