package com.zeroq.back.database.pub.repository;

import com.zeroq.back.database.pub.entity.SensorAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SensorAttachmentRepository extends JpaRepository<SensorAttachment, Long> {
    @Query("SELECT sa FROM SensorAttachment sa WHERE sa.sensor.id = :sensorId AND sa.detachedAt IS NULL")
    Optional<SensorAttachment> findCurrentAttachment(@Param("sensorId") Long sensorId);

    List<SensorAttachment> findBySpaceIdAndActiveTrue(Long spaceId);

    @Query("SELECT sa FROM SensorAttachment sa WHERE sa.space.id = :spaceId AND sa.detachedAt IS NULL")
    List<SensorAttachment> findCurrentAttachmentsInSpace(@Param("spaceId") Long spaceId);

    List<SensorAttachment> findBySensorIdOrderByAttachedAtDesc(Long sensorId);
}
