package com.zeroq.back.database.admin.repository;

import com.zeroq.back.database.admin.entity.AdminOccupancyData;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminOccupancyDataRepository extends JpaRepository<AdminOccupancyData, Long> {
    @EntityGraph(attributePaths = "space")
    Optional<AdminOccupancyData> findBySpaceId(Long spaceId);
}
