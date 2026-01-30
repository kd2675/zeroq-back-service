package com.zeroq.back.service.userlocation.biz;

import auth.common.core.client.UserServiceClient;
import auth.common.core.dto.UserDto;
import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.pub.dto.CreateUserLocationRequest;
import com.zeroq.back.database.pub.dto.UserLocationDTO;
import com.zeroq.back.database.pub.entity.Space;
import com.zeroq.back.database.pub.entity.UserLocation;
import com.zeroq.back.database.pub.repository.SpaceRepository;
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
 * - UserServiceClient를 통해 auth-back-server에서 사용자 정보 조회
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserLocationService {
    private final UserLocationRepository userLocationRepository;
    private final SpaceRepository spaceRepository;
    private final UserServiceClient userServiceClient;

    /**
     * 사용자 위치 정보 생성
     */
    @Transactional
    public UserLocationDTO createUserLocation(CreateUserLocationRequest request) {
        // 1. 사용자 존재 여부 확인 (auth-back-server 호출)
        if (!userServiceClient.existsById(request.getUserId())) {
            throw new LiveSpaceException.ResourceNotFoundException(
                    "User", "id", request.getUserId());
        }

        // 2. 공간 존재 여부 확인
        Space space = spaceRepository.findById(request.getSpaceId())
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException(
                        "Space", "id", request.getSpaceId()));

        // 3. UserLocation 생성
        UserLocation userLocation = UserLocation.builder()
                .userId(request.getUserId())
                .space(space)
                .visitedAt(request.getVisitedAt())
                .leftAt(request.getLeftAt())
                .durationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 0)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .note(request.getNote())
                .build();

        UserLocation saved = userLocationRepository.save(userLocation);
        log.info("UserLocation created: id={}, userId={}, spaceId={}",
                saved.getId(), saved.getUserId(), saved.getSpace().getId());

        // 4. 사용자 정보와 함께 DTO 반환
        UserDto user = userServiceClient.getUserById(request.getUserId());
        return UserLocationDTO.from(saved, user);
    }

    /**
     * 사용자 위치 정보 조회 (with User Info)
     */
    public UserLocationDTO getUserLocationById(Long id) {
        UserLocation userLocation = userLocationRepository.findById(id)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException(
                        "UserLocation", "id", id));

        // UserServiceClient를 통해 사용자 정보 조회
        UserDto user = userServiceClient.getUserById(userLocation.getUserId());
        return UserLocationDTO.from(userLocation, user);
    }

    /**
     * 특정 사용자의 위치 이력 조회 (with User Info)
     */
    public Page<UserLocationDTO> getUserLocationsByUserId(Long userId, Pageable pageable) {
        // 사용자 정보 조회
        UserDto user = userServiceClient.getUserById(userId);

        // 위치 이력 조회
        Page<UserLocation> locations = userLocationRepository.findByUserIdOrderByVisitedAtDesc(userId, pageable);

        // DTO 변환 (사용자 정보 포함)
        return locations.map(location -> UserLocationDTO.from(location, user));
    }

    /**
     * 특정 공간에 대한 사용자의 방문 이력 조회
     */
    public List<UserLocationDTO> getUserVisitsToSpace(Long userId, Long spaceId) {
        // 사용자 정보 조회
        UserDto user = userServiceClient.getUserById(userId);

        // 방문 이력 조회
        List<UserLocation> visits = userLocationRepository.findUserVisitsToSpace(userId, spaceId);

        // DTO 변환 (사용자 정보 포함)
        return visits.stream()
                .map(visit -> UserLocationDTO.from(visit, user))
                .collect(Collectors.toList());
    }

    /**
     * 특정 시간 이후 사용자 위치 조회
     */
    public List<UserLocationDTO> getUserLocationsAfter(Long userId, LocalDateTime startTime) {
        // 사용자 정보 조회
        UserDto user = userServiceClient.getUserById(userId);

        // 위치 이력 조회
        List<UserLocation> locations = userLocationRepository.findUserLocationsAfter(userId, startTime);

        // DTO 변환 (사용자 정보 포함)
        return locations.stream()
                .map(location -> UserLocationDTO.from(location, user))
                .collect(Collectors.toList());
    }

    /**
     * 특정 공간의 총 방문 횟수
     */
    public long countVisitsToSpace(Long spaceId) {
        return userLocationRepository.countVisitsToSpace(spaceId);
    }
}
