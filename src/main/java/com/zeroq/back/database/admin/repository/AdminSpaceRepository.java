package com.zeroq.back.database.admin.repository;

import com.zeroq.back.database.admin.entity.AdminSpace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AdminSpaceRepository extends JpaRepository<AdminSpace, Long> {
    Page<AdminSpace> findByActiveAndVerifiedTrue(boolean active, Pageable pageable);

    Page<AdminSpace> findByActiveTrueAndVerifiedTrueAndOwnerProfileId(Long ownerProfileId, Pageable pageable);

    Page<AdminSpace> findByOwnerProfileId(Long ownerProfileId, Pageable pageable);

    Page<AdminSpace> findByNameContainingIgnoreCaseAndActiveTrue(String name, Pageable pageable);

    @Query("SELECT s FROM AdminSpace s WHERE s.active = true AND s.verified = true ORDER BY s.averageRating DESC")
    Page<AdminSpace> findTopRatedSpaces(Pageable pageable);
}
