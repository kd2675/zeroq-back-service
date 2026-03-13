package com.zeroq.back.database.admin.repository;

import com.zeroq.back.database.admin.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByProfileIdOrderByCreateDateDesc(Long profileId, Pageable pageable);

    Page<Notification> findByProfileIdAndReadFalseOrderByCreateDateDesc(Long profileId, Pageable pageable);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.profileId = :profileId AND n.read = false")
    long countUnreadNotifications(@Param("profileId") Long profileId);

    @Query("SELECT n FROM Notification n WHERE n.profileId = :profileId AND n.type = :type ORDER BY n.createDate DESC")
    Page<Notification> findByUserAndType(
            @Param("profileId") Long profileId,
            @Param("type") Notification.NotificationType type,
            Pageable pageable);
}
