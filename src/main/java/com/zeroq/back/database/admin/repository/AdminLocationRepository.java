package com.zeroq.back.database.admin.repository;

import com.zeroq.back.database.admin.entity.AdminLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminLocationRepository extends JpaRepository<AdminLocation, Long> {
    Optional<AdminLocation> findBySpaceId(Long spaceId);
}
