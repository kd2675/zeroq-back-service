package com.zeroq.back.database.sensor.repository;

import com.zeroq.back.database.sensor.entity.SensorCommand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorCommandRepository extends JpaRepository<SensorCommand, Long> {
    void deleteAllBySensorId(String sensorId);
}
