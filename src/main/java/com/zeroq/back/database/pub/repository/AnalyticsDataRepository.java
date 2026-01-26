package com.zeroq.back.database.pub.repository;

import com.zeroq.back.database.pub.entity.AnalyticsData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalyticsDataRepository extends JpaRepository<AnalyticsData, Long> {
    Page<AnalyticsData> findBySpaceIdOrderByCreateDateDesc(Long spaceId, Pageable pageable);

    List<AnalyticsData> findBySpaceIdAndPeriod(Long spaceId, String period);
}
