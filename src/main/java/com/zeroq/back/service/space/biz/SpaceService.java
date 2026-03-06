package com.zeroq.back.service.space.biz;

import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.pub.dto.CreateSpaceRequest;
import com.zeroq.back.database.pub.entity.Category;
import com.zeroq.back.database.pub.entity.Location;
import com.zeroq.back.database.pub.entity.Space;
import com.zeroq.back.database.pub.repository.CategoryRepository;
import com.zeroq.back.database.pub.repository.LocationRepository;
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
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;

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
     * 공간 검색 (키워드)
     */
    public Page<Space> searchSpaces(String keyword, Pageable pageable) {
        return spaceRepository.findByNameContainingIgnoreCaseAndActiveTrue(keyword, pageable);
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
    public Space createSpace(CreateSpaceRequest request) {
        Category category = getCategoryById(request.getCategoryId());

        Space space = Space.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(category)
                .capacity(request.getCapacity())
                .phoneNumber(request.getPhoneNumber())
                .operatingHours(request.getOperatingHours())
                .imageUrl(request.getImageUrl())
                .active(true)
                .verified(false)
                .averageRating(0.0)
                .build();

        Space savedSpace = spaceRepository.save(space);
        Location location = Location.builder()
                .space(savedSpace)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .address(request.getAddress())
                .placeId(createInternalPlaceId(savedSpace.getId()))
                .build();

        Location savedLocation = locationRepository.save(location);
        savedSpace.setLocation(savedLocation);
        return savedSpace;
    }

    /**
     * 공간 수정 (Admin)
     */
    @Transactional
    public Space updateSpace(Long spaceId, CreateSpaceRequest request) {
        Space space = getSpaceById(spaceId);
        Category category = getCategoryById(request.getCategoryId());

        space.setName(request.getName());
        space.setDescription(request.getDescription());
        space.setCategory(category);
        space.setCapacity(request.getCapacity());
        space.setPhoneNumber(request.getPhoneNumber());
        space.setOperatingHours(request.getOperatingHours());
        space.setImageUrl(request.getImageUrl());

        Location location = locationRepository.findBySpaceId(spaceId)
                .orElseGet(() -> Location.builder()
                        .space(space)
                        .placeId(createInternalPlaceId(space.getId()))
                        .build());
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setAddress(request.getAddress());

        if (location.getPlaceId() == null || location.getPlaceId().isBlank()) {
            location.setPlaceId(createInternalPlaceId(space.getId()));
        }

        Location savedLocation = locationRepository.save(location);
        space.setLocation(savedLocation);
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

    private Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("Category", "id", categoryId));
    }

    private String createInternalPlaceId(Long spaceId) {
        return "zeroq-space-" + spaceId;
    }
}
