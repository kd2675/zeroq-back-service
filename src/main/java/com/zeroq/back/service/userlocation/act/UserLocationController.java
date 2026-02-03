package com.zeroq.back.service.userlocation.act;

import com.zeroq.back.database.pub.dto.CreateUserLocationRequest;
import com.zeroq.back.database.pub.dto.UserLocationDTO;
import com.zeroq.back.service.userlocation.biz.UserLocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import web.common.core.response.base.dto.ResponseDataDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * UserLocation Controller
 * - 사용자 위치 정보 관리 API
 * - UserServiceClient를 통해 auth-back-server의 사용자 정보를 조회하는 예제
 */
@Slf4j
@RestController
@RequestMapping("/api/zeroq/v1/user-locations")
@RequiredArgsConstructor
public class UserLocationController {
    private final UserLocationService userLocationService;

    /**
     * 사용자 위치 정보 생성
     * POST /api/v1/user-locations
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDataDTO<UserLocationDTO> createUserLocation(
            @Valid @RequestBody CreateUserLocationRequest request) {
        log.info("Create user location: userId={}, spaceId={}", request.getUserId(), request.getSpaceId());
        UserLocationDTO dto = userLocationService.createUserLocation(request);
        return ResponseDataDTO.of(dto, "사용자 위치 정보가 생성되었습니다");
    }

    /**
     * 사용자 위치 정보 단건 조회 (사용자 정보 포함)
     * GET /api/v1/user-locations/{id}
     */
    @GetMapping("/{id}")
    public ResponseDataDTO<UserLocationDTO> getUserLocationById(@PathVariable Long id) {
        log.info("Get user location by id: id={}", id);
        UserLocationDTO dto = userLocationService.getUserLocationById(id);
        return ResponseDataDTO.of(dto, "사용자 위치 정보 조회 성공");
    }

    /**
     * 특정 사용자의 위치 이력 조회 (페이징, 사용자 정보 포함)
     * GET /api/v1/user-locations/user/{userId}
     *
     * 예제: UserServiceClient를 통해 auth-back-server에서 사용자 정보를 조회합니다.
     */
    @GetMapping("/user/{userId}")
    public ResponseDataDTO<Page<UserLocationDTO>> getUserLocationsByUserId(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "visitedAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        log.info("Get user locations by userId: userId={}", userId);
        Page<UserLocationDTO> page = userLocationService.getUserLocationsByUserId(userId, pageable);
        return ResponseDataDTO.of(page, "사용자 위치 이력 조회 성공");
    }

    /**
     * 특정 공간에 대한 사용자의 방문 이력 조회
     * GET /api/v1/user-locations/user/{userId}/space/{spaceId}
     */
    @GetMapping("/user/{userId}/space/{spaceId}")
    public ResponseDataDTO<List<UserLocationDTO>> getUserVisitsToSpace(
            @PathVariable Long userId,
            @PathVariable Long spaceId) {
        log.info("Get user visits to space: userId={}, spaceId={}", userId, spaceId);
        List<UserLocationDTO> visits = userLocationService.getUserVisitsToSpace(userId, spaceId);
        return ResponseDataDTO.of(visits, "공간 방문 이력 조회 성공");
    }

    /**
     * 특정 시간 이후 사용자 위치 조회
     * GET /api/v1/user-locations/user/{userId}/after?startTime=2024-01-01T00:00:00
     */
    @GetMapping("/user/{userId}/after")
    public ResponseDataDTO<List<UserLocationDTO>> getUserLocationsAfter(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {
        log.info("Get user locations after: userId={}, startTime={}", userId, startTime);
        List<UserLocationDTO> locations = userLocationService.getUserLocationsAfter(userId, startTime);
        return ResponseDataDTO.of(locations, "사용자 위치 이력 조회 성공");
    }

    /**
     * 특정 공간의 총 방문 횟수
     * GET /api/v1/user-locations/space/{spaceId}/visits/count
     */
    @GetMapping("/space/{spaceId}/visits/count")
    public ResponseDataDTO<Long> countVisitsToSpace(@PathVariable Long spaceId) {
        log.info("Count visits to space: spaceId={}", spaceId);
        long count = userLocationService.countVisitsToSpace(spaceId);
        return ResponseDataDTO.of(count, "공간 방문 횟수 조회 성공");
    }
}
