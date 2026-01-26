package com.zeroq.back.database.pub.repository;

import com.zeroq.back.database.pub.entity.CrowdLevel;
import com.zeroq.back.database.pub.entity.OccupancyData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OccupancyDataRepository extends JpaRepository<OccupancyData, Long> {
    Optional<OccupancyData> findBySpaceId(Long spaceId);

    @Query("SELECT o FROM OccupancyData o WHERE o.crowdLevel = :crowdLevel ORDER BY o.occupancyPercentage DESC")
    List<OccupancyData> findByMostCrowdedSpaces(@Param("crowdLevel") CrowdLevel crowdLevel);

    @Query("SELECT o FROM OccupancyData o ORDER BY o.occupancyPercentage DESC LIMIT :limit")
    List<OccupancyData> findMostCrowdedSpaces(@Param("limit") int limit);
}
