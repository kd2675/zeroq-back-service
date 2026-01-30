package com.zeroq.back.database.pub.repository;

import com.zeroq.back.database.pub.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserIdAndSpaceId(Long userId, Long spaceId);

    Page<Favorite> findByUserIdOrderByOrderAsc(Long userId, Pageable pageable);

    @Query("SELECT f FROM Favorite f WHERE f.userId = :userId ORDER BY f.order ASC")
    Page<Favorite> findUserFavorites(@Param("userId") Long userId, Pageable pageable);

    long countByUserId(Long userId);
}
