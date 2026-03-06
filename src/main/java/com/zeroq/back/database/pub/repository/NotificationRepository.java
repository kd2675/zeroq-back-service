package com.zeroq.back.database.pub.repository;

import com.zeroq.back.database.pub.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUserKeyOrderByCreateDateDesc(String userKey, Pageable pageable);

    Page<Notification> findByUserKeyAndReadFalseOrderByCreateDateDesc(String userKey, Pageable pageable);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userKey = :userKey AND n.read = false")
    long countUnreadNotifications(@Param("userKey") String userKey);

    @Query("SELECT n FROM Notification n WHERE n.userKey = :userKey AND n.type = :type ORDER BY n.createDate DESC")
    Page<Notification> findByUserAndType(
            @Param("userKey") String userKey,
            @Param("type") Notification.NotificationType type,
            Pageable pageable);
}
