package com.zeroq.back.service.space.biz;

import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.admin.entity.AdminLocation;
import com.zeroq.back.database.admin.entity.AdminSpace;
import com.zeroq.back.database.admin.repository.AdminLocationRepository;
import com.zeroq.back.database.admin.repository.AdminSpaceRepository;
import com.zeroq.back.database.pub.dto.CreateSpaceRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpaceService {
    private final AdminSpaceRepository spaceRepository;
    private final AdminLocationRepository locationRepository;

    /**
     * 공간 목록 조회 (활성화 & 검증된)
     */
    public Page<AdminSpace> getActiveSpaces(Pageable pageable) {
        return spaceRepository.findByActiveAndVerifiedTrue(true, pageable);
    }

    public Page<AdminSpace> getManagedSpaces(Long ownerProfileId, boolean adminView, Pageable pageable) {
        if (adminView) {
            return getActiveSpaces(pageable);
        }
        return spaceRepository.findByActiveTrueAndVerifiedTrueAndOwnerProfileId(ownerProfileId, pageable);
    }

    public Page<AdminSpace> getWorkspaceManagedSpaces(Long ownerProfileId, boolean adminView, Pageable pageable) {
        if (adminView) {
            return spaceRepository.findAll(pageable);
        }
        return spaceRepository.findByOwnerProfileId(ownerProfileId, pageable);
    }

    /**
     * 공간 상세 조회
     */
    public AdminSpace getSpaceById(Long spaceId) {
        return spaceRepository.findById(spaceId)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("Space", "id", spaceId));
    }

    /**
     * 공간 검색 (키워드)
     */
    public Page<AdminSpace> searchSpaces(String keyword, Pageable pageable) {
        return spaceRepository.findByNameContainingIgnoreCaseAndActiveTrue(keyword, pageable);
    }

    /**
     * 평점 높은 공간 조회
     */
    public Page<AdminSpace> getTopRatedSpaces(Pageable pageable) {
        return spaceRepository.findTopRatedSpaces(pageable);
    }

    /**
     * 공간 생성 (Admin)
     */
    @Transactional
    public AdminSpace createSpace(CreateSpaceRequest request, Long ownerProfileId) {
        AdminSpace space = AdminSpace.builder()
                .spaceCode(createSpaceCode(request.getName()))
                .name(request.getName())
                .description(request.getDescription())
                .ownerProfileId(ownerProfileId)
                .operationalStatus(defaultText(request.getOperationalStatus(), "ACTIVE"))
                .phoneNumber(request.getPhoneNumber())
                .operatingHours(request.getOperatingHours())
                .imageUrl(request.getImageUrl())
                .active(true)
                .verified(false)
                .averageRating(0.0)
                .build();

        AdminSpace savedSpace = spaceRepository.save(space);
        AdminLocation location = AdminLocation.builder()
                .space(savedSpace)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .address(request.getAddress())
                .placeId(createInternalPlaceId(savedSpace.getId()))
                .build();

        AdminLocation savedLocation = locationRepository.save(location);
        savedSpace.setLocation(savedLocation);
        return savedSpace;
    }

    /**
     * 공간 수정 (Admin)
     */
    @Transactional
    public AdminSpace updateSpace(Long spaceId, CreateSpaceRequest request, Long requesterProfileId, boolean adminView) {
        AdminSpace space = getOwnedSpace(spaceId, requesterProfileId, adminView);

        space.setName(request.getName());
        space.setDescription(request.getDescription());
        space.setPhoneNumber(request.getPhoneNumber());
        space.setOperatingHours(request.getOperatingHours());
        space.setImageUrl(request.getImageUrl());
        if (!StringUtils.hasText(space.getSpaceCode())) {
            space.setSpaceCode(createSpaceCode(request.getName()));
        }
        space.setOperationalStatus(defaultText(request.getOperationalStatus(), defaultText(space.getOperationalStatus(), space.isActive() ? "ACTIVE" : "INACTIVE")));

        AdminLocation location = locationRepository.findBySpaceId(spaceId)
                .orElseGet(() -> AdminLocation.builder()
                        .space(space)
                        .placeId(createInternalPlaceId(space.getId()))
                        .build());
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setAddress(request.getAddress());

        if (location.getPlaceId() == null || location.getPlaceId().isBlank()) {
            location.setPlaceId(createInternalPlaceId(space.getId()));
        }

        AdminLocation savedLocation = locationRepository.save(location);
        space.setLocation(savedLocation);
        return spaceRepository.save(space);
    }

    /**
     * 공간 삭제 (Admin - 논리 삭제)
     */
    @Transactional
    public void deleteSpace(Long spaceId, Long requesterProfileId, boolean adminView) {
        AdminSpace space = getOwnedSpace(spaceId, requesterProfileId, adminView);
        space.setActive(false);
        spaceRepository.save(space);
    }

    public AdminSpace getOwnedSpace(Long spaceId, Long requesterProfileId, boolean adminView) {
        AdminSpace space = getSpaceById(spaceId);
        if (!adminView && !space.getOwnerProfileId().equals(requesterProfileId)) {
            throw new LiveSpaceException.ForbiddenException("관리 권한이 없는 공간입니다");
        }
        return space;
    }

    private String createInternalPlaceId(Long spaceId) {
        return "zeroq-space-" + spaceId;
    }

    private String createSpaceCode(String name) {
        String seed = StringUtils.hasText(name) ? name.replaceAll("[^A-Za-z0-9]+", "-").toUpperCase() : "SPACE";
        if (seed.isBlank()) {
            seed = "SPACE";
        }
        String normalized = seed.replaceAll("(^-|-$)", "");
        return (normalized.length() > 32 ? normalized.substring(0, 32) : normalized)
                + "-"
                + Long.toUnsignedString(System.nanoTime(), 36).toUpperCase();
    }

    private String defaultText(String value, String fallback) {
        return StringUtils.hasText(value) ? value.trim() : fallback;
    }
}
