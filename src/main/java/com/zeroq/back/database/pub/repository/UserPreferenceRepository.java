package com.zeroq.back.database.pub.repository;

import com.zeroq.back.database.pub.entity.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
    Optional<UserPreference> findByUserKeyAndPreferenceKey(String userKey, String preferenceKey);

    List<UserPreference> findByUserKey(String userKey);
}
