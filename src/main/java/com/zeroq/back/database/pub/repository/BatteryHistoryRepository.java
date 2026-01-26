package com.zeroq.back.database.pub.repository;

import com.zeroq.back.database.pub.entity.BatteryHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatteryHistoryRepository extends JpaRepository<BatteryHistory, Long> {
    Page<BatteryHistory> findBySensorIdOrderByCreateDateDesc(Long sensorId, Pageable pageable);
}
