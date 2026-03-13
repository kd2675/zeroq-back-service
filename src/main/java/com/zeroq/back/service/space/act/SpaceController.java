package com.zeroq.back.service.space.act;

import auth.common.core.context.UserContext;
import web.common.core.response.base.dto.ResponseDataDTO;
import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.admin.entity.AdminSpace;
import com.zeroq.back.database.pub.dto.CreateSpaceRequest;
import com.zeroq.back.database.pub.dto.SpaceDTO;
import com.zeroq.back.database.pub.repository.ReviewRepository;
import com.zeroq.back.service.admin.biz.AdminProfileService;
import com.zeroq.back.service.space.biz.SpaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/zeroq/v1/spaces")
@RequiredArgsConstructor
public class SpaceController {
    private final SpaceService spaceService;
    private final ReviewRepository reviewRepository;
    private final AdminProfileService adminProfileService;

    /**
     * 공간 목록 조회 (활성화 & 검증된)
     * GET /api/v1/spaces?page=0&size=20
     */
    @GetMapping
    public ResponseDataDTO<Page<SpaceDTO>> getSpaces(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get spaces request: page={}, size={}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<AdminSpace> spaces = spaceService.getActiveSpaces(pageable);

        Page<SpaceDTO> dtos = spaces.map(this::convertToDTO);

        return ResponseDataDTO.of(dtos, "공간 목록 조회 성공");
    }

    /**
     * 공간 상세 조회
     * GET /api/v1/spaces/{id}
     */
    @GetMapping("/{id}")
    public ResponseDataDTO<SpaceDTO> getSpaceById(@PathVariable Long id) {
        log.info("Get space request: id={}", id);

        AdminSpace space = spaceService.getSpaceById(id);
        SpaceDTO dto = convertToDTO(space);

        return ResponseDataDTO.of(dto, "공간 상세 조회 성공");
    }

    /**
     * 공간 검색
     * GET /api/v1/spaces/search?keyword=카페&page=0&size=20
     */
    @GetMapping("/search")
    public ResponseDataDTO<Page<SpaceDTO>> searchSpaces(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Search spaces: keyword={}, page={}, size={}", keyword, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<AdminSpace> spaces = spaceService.searchSpaces(keyword, pageable);

        Page<SpaceDTO> dtos = spaces.map(this::convertToDTO);

        return ResponseDataDTO.of(dtos, "공간 검색 성공");
    }

    /**
     * 평점 높은 공간 조회
     * GET /api/v1/spaces/top-rated?page=0&size=20
     */
    @GetMapping("/top-rated")
    public ResponseDataDTO<Page<SpaceDTO>> getTopRatedSpaces(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get top rated spaces: page={}, size={}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<AdminSpace> spaces = spaceService.getTopRatedSpaces(pageable);

        Page<SpaceDTO> dtos = spaces.map(this::convertToDTO);

        return ResponseDataDTO.of(dtos, "평점 높은 공간 조회 성공");
    }

    /**
     * 공간 생성 (Admin)
     * POST /api/v1/spaces
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDataDTO<SpaceDTO> createSpace(
            @Valid @RequestBody CreateSpaceRequest request,
            UserContext userContext
    ) {
        Long profileId = resolveAdminProfileId(userContext);
        log.info("Create space request: name={}", request.getName());

        AdminSpace createdSpace = spaceService.createSpace(request, profileId);
        SpaceDTO dto = convertToDTO(createdSpace);

        return ResponseDataDTO.of(dto, "공간이 생성되었습니다");
    }

    /**
     * 공간 수정 (Admin)
     * PUT /api/v1/spaces/{id}
     */
    @PutMapping("/{id}")
    public ResponseDataDTO<SpaceDTO> updateSpace(
            @PathVariable Long id,
            @Valid @RequestBody CreateSpaceRequest request,
            UserContext userContext) {
        Long profileId = resolveAdminProfileId(userContext);
        log.info("Update space request: id={}, name={}", id, request.getName());

        AdminSpace updatedSpace = spaceService.updateSpace(id, request, profileId, userContext.isAdmin());
        SpaceDTO dto = convertToDTO(updatedSpace);

        return ResponseDataDTO.of(dto, "공간이 수정되었습니다");
    }

    /**
     * 공간 삭제 (Admin)
     * DELETE /api/v1/spaces/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseDataDTO<Void> deleteSpace(@PathVariable Long id, UserContext userContext) {
        Long profileId = resolveAdminProfileId(userContext);
        log.info("Delete space request: id={}", id);

        spaceService.deleteSpace(id, profileId, userContext.isAdmin());

        return ResponseDataDTO.of(null, "공간이 삭제되었습니다");
    }

    /**
     * DTO 변환
     */
    private SpaceDTO convertToDTO(AdminSpace space) {
        Double averageRating = reviewRepository.getAverageRating(space.getId());
        long reviewCount = reviewRepository.countReviewsBySpace(space.getId());

        return SpaceDTO.builder()
                .id(space.getId())
                .name(space.getName())
                .description(space.getDescription())
                .latitude(space.getLocation() != null ? space.getLocation().getLatitude() : 0.0)
                .longitude(space.getLocation() != null ? space.getLocation().getLongitude() : 0.0)
                .address(space.getLocation() != null ? space.getLocation().getAddress() : "")
                .averageRating(averageRating != null ? averageRating : 0.0)
                .reviewCount((int) reviewCount)
                .imageUrl(space.getImageUrl())
                .verified(space.isVerified())
                .build();
    }

    private Long resolveAdminProfileId(UserContext userContext) {
        if (userContext == null || !userContext.isAuthenticated()) {
            throw new LiveSpaceException.UnauthorizedException("Login required");
        }
        if (!userContext.isManager() && !userContext.isAdmin()) {
            throw new LiveSpaceException.ForbiddenException("MANAGER or ADMIN role required");
        }
        if (userContext.getUserKey() == null || userContext.getUserKey().isBlank()) {
            throw new LiveSpaceException.ForbiddenException("인증 사용자 정보가 없습니다");
        }
        return adminProfileService.resolveProfileId(
                userContext.getUserKey(),
                userContext.getUserName(),
                userContext.getRole()
        );
    }
}
