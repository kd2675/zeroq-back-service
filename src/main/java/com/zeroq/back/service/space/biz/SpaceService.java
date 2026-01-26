package com.zeroq.back.service.space.biz;

import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.pub.entity.Space;
import com.zeroq.back.database.pub.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpaceService {
    private final SpaceRepository spaceRepository;

    /**
     * 공간 목록 조회 (활성화 & 검증된)
     */
    public Page<Space> getActiveSpaces(Pageable pageable) {
        return spaceRepository.findByActiveAndVerifiedTrue(true, pageable);
    }

    /**
     * 공간 상세 조회
     */
    public Space getSpaceById(Long spaceId) {
        return spaceRepository.findById(spaceId)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("Space", "id", spaceId));
    }

    /**
     * 카테고리별 공간 조회
     */
    public Page<Space> getSpacesByCategory(Long categoryId, Pageable pageable) {
        return spaceRepository.findByCategoryIdAndActiveTrue(categoryId, pageable);
    }

    /**
     * 공간 검색 (키워드 & 카테고리)
     */
    public Page<Space> searchSpaces(String keyword, Long categoryId, Pageable pageable) {
        return spaceRepository.searchByKeywordAndCategory(keyword, categoryId, pageable);
    }

    /**
     * 평점 높은 공간 조회
     */
    public Page<Space> getTopRatedSpaces(Pageable pageable) {
        return spaceRepository.findTopRatedSpaces(pageable);
    }

    /**
     * 공간 생성 (Admin)
     */
    @Transactional
    public Space createSpace(Space space) {
        return spaceRepository.save(space);
    }

    /**
     * 공간 수정 (Admin)
     */
    @Transactional
    public Space updateSpace(Long spaceId, Space updatedSpace) {
        Space space = getSpaceById(spaceId);
        space.setName(updatedSpace.getName());
        space.setDescription(updatedSpace.getDescription());
        space.setCapacity(updatedSpace.getCapacity());
        space.setPhoneNumber(updatedSpace.getPhoneNumber());
        space.setOperatingHours(updatedSpace.getOperatingHours());
        return spaceRepository.save(space);
    }

    /**
     * 공간 삭제 (Admin - 논리 삭제)
     */
    @Transactional
    public void deleteSpace(Long spaceId) {
        Space space = getSpaceById(spaceId);
        space.setActive(false);
        spaceRepository.save(space);
    }
}
