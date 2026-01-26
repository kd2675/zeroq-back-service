package com.zeroq.back.database.pub.repository;

import com.zeroq.back.database.pub.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNameAndActiveTrue(String name);

    List<Category> findByActiveTrue();
}
