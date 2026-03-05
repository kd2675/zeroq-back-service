package com.zeroq.back.service.userlocation.act;

import auth.common.core.context.UserContext;
import com.zeroq.back.database.pub.dto.CreateUserLocationRequest;
import com.zeroq.back.database.pub.dto.UserLocationDTO;
import com.zeroq.back.common.exception.LiveSpaceException;
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
 * - Muse와 동일하게 Gateway UserContext(userId) 기반으로 동작
 */
@Slf4j
@RestController
@RequestMapping("/api/zeroq/v1/user-locations")
@RequiredArgsConstructor
public class UserLocationController {
    private final UserLocationService userLocationService;

    /**
     * 사용자 위치 정보 생성
     * POST /api/zeroq/v1/user-locations
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDataDTO<UserLocationDTO> createUserLocation(
            @Valid @RequestBody CreateUserLocationRequest request,
            UserContext userContext) {
        Long userId = requireUserId(userContext);
        log.info("Create user location: userId={}, spaceId={}", userId, request.getSpaceId());
        UserLocationDTO dto = userLocationService.createUserLocation(userId, request);
        return ResponseDataDTO.of(dto, "사용자 위치 정보가 생성되었습니다");
    }

    /**
     * 사용자 위치 정보 단건 조회 (본인 소유)
     * GET /api/zeroq/v1/user-locations/{id}
     */
    @GetMapping("/{id}")
    public ResponseDataDTO<UserLocationDTO> getUserLocationById(
            @PathVariable Long id,
            UserContext userContext) {
        Long userId = requireUserId(userContext);
        log.info("Get my user location by id: userId={}, id={}", userId, id);
        UserLocationDTO dto = userLocationService.getUserLocationById(userId, id);
        return ResponseDataDTO.of(dto, "사용자 위치 정보 조회 성공");
    }

    /**
     * 내 위치 이력 조회 (페이징)
     * GET /api/zeroq/v1/user-locations/me
     */
    @GetMapping("/me")
    public ResponseDataDTO<Page<UserLocationDTO>> getMyLocations(
            UserContext userContext,
            @PageableDefault(size = 20, sort = "visitedAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Long userId = requireUserId(userContext);
        log.info("Get my locations: userId={}", userId);
        Page<UserLocationDTO> page = userLocationService.getMyUserLocations(userId, pageable);
        return ResponseDataDTO.of(page, "사용자 위치 이력 조회 성공");
    }

    /**
     * 특정 공간에 대한 내 방문 이력 조회
     * GET /api/zeroq/v1/user-locations/me/space/{spaceId}
     */
    @GetMapping("/me/space/{spaceId}")
    public ResponseDataDTO<List<UserLocationDTO>> getMyVisitsToSpace(
            UserContext userContext,
            @PathVariable Long spaceId) {
        Long userId = requireUserId(userContext);
        log.info("Get user visits to space: userId={}, spaceId={}", userId, spaceId);
        List<UserLocationDTO> visits = userLocationService.getMyVisitsToSpace(userId, spaceId);
        return ResponseDataDTO.of(visits, "공간 방문 이력 조회 성공");
    }

    /**
     * 특정 시간 이후 내 위치 조회
     * GET /api/zeroq/v1/user-locations/me/after?startTime=2024-01-01T00:00:00
     */
    @GetMapping("/me/after")
    public ResponseDataDTO<List<UserLocationDTO>> getMyLocationsAfter(
            UserContext userContext,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {
        Long userId = requireUserId(userContext);
        log.info("Get user locations after: userId={}, startTime={}", userId, startTime);
        List<UserLocationDTO> locations = userLocationService.getMyLocationsAfter(userId, startTime);
        return ResponseDataDTO.of(locations, "사용자 위치 이력 조회 성공");
    }

    /**
     * 특정 공간의 총 방문 횟수
     * GET /api/zeroq/v1/user-locations/space/{spaceId}/visits/count
     */
    @GetMapping("/space/{spaceId}/visits/count")
    public ResponseDataDTO<Long> countVisitsToSpace(@PathVariable Long spaceId) {
        log.info("Count visits to space: spaceId={}", spaceId);
        long count = userLocationService.countVisitsToSpace(spaceId);
        return ResponseDataDTO.of(count, "공간 방문 횟수 조회 성공");
    }

    private Long requireUserId(UserContext userContext) {
        if (userContext == null || !userContext.isAuthenticated()) {
            throw new LiveSpaceException.UnauthorizedException("Login required");
        }
        return userContext.getUserId();
    }
}
