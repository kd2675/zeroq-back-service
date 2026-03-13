package com.zeroq.back.database.sensor.repository;

import com.zeroq.back.database.sensor.entity.SensorTelemetry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SensorTelemetryRepository extends JpaRepository<SensorTelemetry, Long> {
    List<SensorTelemetry> findTop200BySensorIdOrderByMeasuredAtDesc(String sensorId);

    void deleteAllBySensorId(String sensorId);
}
