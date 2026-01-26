package com.zeroq.back.database.pub.repository;

import com.zeroq.back.database.pub.entity.BatteryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BatteryStatusRepository extends JpaRepository<BatteryStatus, Long> {
    Optional<BatteryStatus> findBySensorId(Long sensorId);

    @Query("SELECT bs FROM BatteryStatus bs WHERE bs.level = :level")
    List<BatteryStatus> findByLevel(@Param("level") BatteryStatus.BatteryStatusLevel level);

    @Query("SELECT bs FROM BatteryStatus bs WHERE bs.currentPercentage < :threshold")
    List<BatteryStatus> findLowBatterySensors(@Param("threshold") double threshold);
}
