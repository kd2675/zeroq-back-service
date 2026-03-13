package com.zeroq.back.database.pub.repository;

import com.zeroq.back.database.pub.entity.UserLocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserLocationRepository extends JpaRepository<UserLocation, Long> {
    Page<UserLocation> findByProfileIdOrderByVisitedAtDesc(Long profileId, Pageable pageable);

    @Query("SELECT ul FROM UserLocation ul WHERE ul.profileId = :profileId AND ul.spaceId = :spaceId ORDER BY ul.visitedAt DESC")
    List<UserLocation> findUserVisitsToSpace(@Param("profileId") Long profileId, @Param("spaceId") Long spaceId);

    @Query("SELECT ul FROM UserLocation ul WHERE ul.profileId = :profileId AND ul.visitedAt >= :startTime ORDER BY ul.visitedAt DESC")
    List<UserLocation> findUserLocationsAfter(@Param("profileId") Long profileId, @Param("startTime") LocalDateTime startTime);

    @Query("SELECT COUNT(ul) FROM UserLocation ul WHERE ul.spaceId = :spaceId")
    long countVisitsToSpace(@Param("spaceId") Long spaceId);
}
