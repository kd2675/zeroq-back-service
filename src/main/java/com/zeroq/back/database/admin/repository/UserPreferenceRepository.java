package com.zeroq.back.database.admin.repository;

import com.zeroq.back.database.admin.entity.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
    Optional<UserPreference> findByProfileIdAndPreferenceKey(Long profileId, String preferenceKey);

    List<UserPreference> findByProfileId(Long profileId);
}
