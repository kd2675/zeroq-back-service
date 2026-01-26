package com.zeroq.back.database.pub.repository;

import com.zeroq.back.database.pub.entity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {
    List<Amenity> findBySpaceIdAndAvailableTrue(Long spaceId);

    List<Amenity> findBySpaceId(Long spaceId);
}
