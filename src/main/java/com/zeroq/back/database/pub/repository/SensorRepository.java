package com.zeroq.back.database.pub.repository;

import com.zeroq.back.database.pub.entity.Sensor;
import com.zeroq.back.database.pub.entity.SensorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    Optional<Sensor> findBySensorId(String sensorId);

    Optional<Sensor> findByMacAddress(String macAddress);

    List<Sensor> findByTypeAndActiveTrue(SensorType type);

    @Query("SELECT s FROM Sensor s WHERE s.active = true AND s.batteryPercentage < :threshold")
    List<Sensor> findLowBatterySensors(@Param("threshold") double threshold);

    @Query("SELECT s FROM Sensor s WHERE s.active = true ORDER BY s.lastHeartbeat DESC")
    List<Sensor> findActiveSensors();

    List<Sensor> findByActiveTrue();
}
