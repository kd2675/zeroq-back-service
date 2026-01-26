package com.zeroq.back.database.pub.repository;

import com.zeroq.back.database.pub.entity.UserBehavior;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserBehaviorRepository extends JpaRepository<UserBehavior, Long> {
    Optional<UserBehavior> findByUserId(Long userId);
}
