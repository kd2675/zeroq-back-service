package com.zeroq.back.database.pub.repository;

import com.zeroq.back.database.pub.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findBySpaceIdAndDeletedFalseOrderByCreateDateDesc(Long spaceId, Pageable pageable);

    Page<Review> findByUserIdAndDeletedFalseOrderByCreateDateDesc(Long userId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.space.id = :spaceId AND r.deleted = false")
    Double getAverageRating(@Param("spaceId") Long spaceId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.space.id = :spaceId AND r.deleted = false")
    long countReviewsBySpace(@Param("spaceId") Long spaceId);
}
