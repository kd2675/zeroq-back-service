package com.zeroq.back.database.pub.repository;

import com.zeroq.back.database.pub.entity.Space;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {
    Page<Space> findByActiveAndVerifiedTrue(boolean active, Pageable pageable);

    Page<Space> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);

    Page<Space> findByNameContainingIgnoreCaseAndActiveTrue(String name, Pageable pageable);

    @Query("SELECT s FROM Space s WHERE s.active = true AND s.category.id = :categoryId AND " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) ")
    Page<Space> searchByKeywordAndCategory(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            Pageable pageable);

    @Query("SELECT s FROM Space s WHERE s.active = true AND s.verified = true ORDER BY s.averageRating DESC")
    Page<Space> findTopRatedSpaces(Pageable pageable);

    List<Space> findByCategoryIdAndActiveTrue(Long categoryId);
}
