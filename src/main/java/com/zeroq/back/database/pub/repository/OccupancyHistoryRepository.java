package com.zeroq.back.database.pub.repository;

import com.zeroq.back.database.pub.entity.OccupancyHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OccupancyHistoryRepository extends JpaRepository<OccupancyHistory, Long> {
    Page<OccupancyHistory> findBySpaceIdOrderByCreateDateDesc(Long spaceId, Pageable pageable);

    @Query("SELECT o FROM OccupancyHistory o WHERE o.space.id = :spaceId AND o.createDate >= :startTime AND o.createDate <= :endTime ORDER BY o.createDate DESC")
    List<OccupancyHistory> findBySpaceIdAndDateRange(
            @Param("spaceId") Long spaceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query("SELECT AVG(o.occupancyPercentage) FROM OccupancyHistory o WHERE o.space.id = :spaceId AND o.createDate >= :startTime")
    Double getAverageOccupancy(@Param("spaceId") Long spaceId, @Param("startTime") LocalDateTime startTime);
}
