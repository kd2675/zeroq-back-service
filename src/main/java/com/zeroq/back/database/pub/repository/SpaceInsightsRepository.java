package com.zeroq.back.database.pub.repository;

import com.zeroq.back.database.pub.entity.SpaceInsights;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpaceInsightsRepository extends JpaRepository<SpaceInsights, Long> {
    Optional<SpaceInsights> findBySpaceId(Long spaceId);
}
