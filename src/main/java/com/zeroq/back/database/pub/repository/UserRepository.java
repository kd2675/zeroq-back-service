package com.zeroq.back.database.pub.repository;

import com.zeroq.back.database.pub.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    @Query("SELECT u FROM User u WHERE u.active = true AND u.deleted = false")
    Page<User> findActiveUsers(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.active = true AND u.deleted = false")
    Page<User> findByRole(@Param("role") User.UserRole role, Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.active = true AND u.deleted = false")
    long countActiveUsers();
}
