package com.zeroq.back.database.admin.repository;

import com.zeroq.back.database.admin.entity.AdminOccupancyHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminOccupancyHistoryRepository extends JpaRepository<AdminOccupancyHistory, Long> {
    @EntityGraph(attributePaths = "space")
    Page<AdminOccupancyHistory> findBySpaceIdOrderByCreateDateDesc(Long spaceId, Pageable pageable);

    @Query("SELECT oh FROM AdminOccupancyHistory oh WHERE oh.space.id = :spaceId " +
            "AND oh.createDate BETWEEN :startTime AND :endTime ORDER BY oh.createDate DESC")
    List<AdminOccupancyHistory> findBySpaceIdAndDateRange(
            @Param("spaceId") Long spaceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT AVG(oh.occupancyPercentage) FROM AdminOccupancyHistory oh " +
            "WHERE oh.space.id = :spaceId AND oh.createDate >= :startTime")
    Double getAverageOccupancy(@Param("spaceId") Long spaceId, @Param("startTime") LocalDateTime startTime);
}
