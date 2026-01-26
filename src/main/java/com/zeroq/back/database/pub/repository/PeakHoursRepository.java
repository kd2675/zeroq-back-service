package com.zeroq.back.database.pub.repository;

import com.zeroq.back.database.pub.entity.PeakHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PeakHoursRepository extends JpaRepository<PeakHours, Long> {
    List<PeakHours> findBySpaceIdOrderByDayOfWeekAscHourOfDayAsc(Long spaceId);

    Optional<PeakHours> findBySpaceIdAndDayOfWeekAndHourOfDay(Long spaceId, int dayOfWeek, int hourOfDay);

    @Query("SELECT p FROM PeakHours p WHERE p.space.id = :spaceId AND p.dayOfWeek = :dayOfWeek ORDER BY p.peakOccupancy DESC LIMIT 1")
    Optional<PeakHours> findPeakHourOfDay(@Param("spaceId") Long spaceId, @Param("dayOfWeek") int dayOfWeek);
}
