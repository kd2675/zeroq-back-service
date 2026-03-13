package com.zeroq.back.database.pub.repository;

import com.zeroq.back.database.pub.entity.ProfileUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileUserRepository extends JpaRepository<ProfileUser, Long> {
    Optional<ProfileUser> findByUserKey(String userKey);
}
