package com.zeroq.back.service.userlocation.biz;

import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.admin.repository.AdminSpaceRepository;
import com.zeroq.back.database.pub.dto.CreateUserLocationRequest;
import com.zeroq.back.database.pub.dto.UserLocationDTO;
import com.zeroq.back.database.pub.entity.UserLocation;
import com.zeroq.back.database.pub.repository.UserLocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserLocation Service
 * - 사용자 위치 정보 관리
 * - Gateway userKey를 profileId로 매핑 후 profileId만 사용
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserLocationService {
    private final UserLocationRepository userLocationRepository;
    private final AdminSpaceRepository adminSpaceRepository;

    /**
     * 사용자 위치 정보 생성
     */
    @Transactional
    public UserLocationDTO createUserLocation(Long profileId, CreateUserLocationRequest request) {
        // 1. 공간 존재 여부 확인
        adminSpaceRepository.findById(request.getSpaceId())
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException(
                        "Space", "id", request.getSpaceId()));

        // 2. UserLocation 생성 (인증 profileId 사용)
        UserLocation userLocation = UserLocation.builder()
                .profileId(profileId)
                .spaceId(request.getSpaceId())
                .visitedAt(request.getVisitedAt())
                .leftAt(request.getLeftAt())
                .durationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 0)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .note(request.getNote())
                .build();

        UserLocation saved = userLocationRepository.save(userLocation);
        log.info("UserLocation created: id={}, profileId={}, spaceId={}",
                saved.getId(), saved.getProfileId(), saved.getSpaceId());

        return UserLocationDTO.from(saved);
    }

    /**
     * 사용자 위치 정보 조회 (본인 소유 검증)
     */
    public UserLocationDTO getUserLocationById(Long profileId, Long id) {
        UserLocation userLocation = userLocationRepository.findById(id)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException(
                        "UserLocation", "id", id));

        if (!userLocation.getProfileId().equals(profileId)) {
            throw new LiveSpaceException.ForbiddenException("본인 위치 정보만 조회할 수 있습니다");
        }

        return UserLocationDTO.from(userLocation);
    }

    /**
     * 내 위치 이력 조회
     */
    public Page<UserLocationDTO> getMyUserLocations(Long profileId, Pageable pageable) {
        Page<UserLocation> locations = userLocationRepository.findByProfileIdOrderByVisitedAtDesc(profileId, pageable);
        return locations.map(UserLocationDTO::from);
    }

    /**
     * 특정 공간에 대한 내 방문 이력 조회
     */
    public List<UserLocationDTO> getMyVisitsToSpace(Long profileId, Long spaceId) {
        List<UserLocation> visits = userLocationRepository.findUserVisitsToSpace(profileId, spaceId);
        return visits.stream()
                .map(UserLocationDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 시간 이후 내 위치 이력 조회
     */
    public List<UserLocationDTO> getMyLocationsAfter(Long profileId, LocalDateTime startTime) {
        List<UserLocation> locations = userLocationRepository.findUserLocationsAfter(profileId, startTime);
        return locations.stream()
                .map(UserLocationDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 공간의 총 방문 횟수
     */
    public long countVisitsToSpace(Long spaceId) {
        return userLocationRepository.countVisitsToSpace(spaceId);
    }
}
