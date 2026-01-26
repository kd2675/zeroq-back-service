package com.zeroq.back.database.pub.repository;

import com.zeroq.back.database.pub.entity.NfcTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NfcTagRepository extends JpaRepository<NfcTag, Long> {
    Optional<NfcTag> findByNfcId(String nfcId);

    List<NfcTag> findBySpaceIdAndActiveTrue(Long spaceId);

    List<NfcTag> findByActiveTrue();
}
