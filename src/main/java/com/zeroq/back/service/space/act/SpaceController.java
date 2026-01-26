package com.zeroq.back.service.space.act;

import com.zeroq.core.response.base.dto.ResponseDataDTO;
import com.zeroq.back.database.pub.dto.CreateSpaceRequest;
import com.zeroq.back.database.pub.dto.SpaceDTO;
import com.zeroq.back.database.pub.entity.Space;
import com.zeroq.back.service.space.biz.SpaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/spaces")
@RequiredArgsConstructor
public class SpaceController {
    private final SpaceService spaceService;

    /**
     * 공간 목록 조회 (활성화 & 검증된)
     * GET /api/v1/spaces?page=0&size=20
     */
    @GetMapping
    public ResponseEntity<ResponseDataDTO<Page<SpaceDTO>>> getSpaces(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get spaces request: page={}, size={}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Space> spaces = spaceService.getActiveSpaces(pageable);
        
        Page<SpaceDTO> dtos = spaces.map(this::convertToDTO);
        
        return ResponseEntity.ok(ResponseDataDTO.of(dtos, "공간 목록 조회 성공"));
    }

    /**
     * 공간 상세 조회
     * GET /api/v1/spaces/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDataDTO<SpaceDTO>> getSpaceById(@PathVariable Long id) {
        log.info("Get space request: id={}", id);
        
        Space space = spaceService.getSpaceById(id);
        SpaceDTO dto = convertToDTO(space);
        
        return ResponseEntity.ok(ResponseDataDTO.of(dto, "공간 상세 조회 성공"));
    }

    /**
     * 카테고리별 공간 조회
     * GET /api/v1/spaces/category/{categoryId}?page=0&size=20
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ResponseDataDTO<Page<SpaceDTO>>> getSpacesByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get spaces by category: categoryId={}, page={}, size={}", categoryId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Space> spaces = spaceService.getSpacesByCategory(categoryId, pageable);
        
        Page<SpaceDTO> dtos = spaces.map(this::convertToDTO);
        
        return ResponseEntity.ok(ResponseDataDTO.of(dtos, "카테고리별 공간 조회 성공"));
    }

    /**
     * 공간 검색
     * GET /api/v1/spaces/search?keyword=카페&categoryId=1&page=0&size=20
     */
    @GetMapping("/search")
    public ResponseEntity<ResponseDataDTO<Page<SpaceDTO>>> searchSpaces(
            @RequestParam String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Search spaces: keyword={}, categoryId={}, page={}, size={}", keyword, categoryId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Space> spaces;
        
        if (categoryId != null) {
            spaces = spaceService.searchSpaces(keyword, categoryId, pageable);
        } else {
            spaces = spaceService.getActiveSpaces(pageable); // fallback
        }
        
        Page<SpaceDTO> dtos = spaces.map(this::convertToDTO);
        
        return ResponseEntity.ok(ResponseDataDTO.of(dtos, "공간 검색 성공"));
    }

    /**
     * 평점 높은 공간 조회
     * GET /api/v1/spaces/top-rated?page=0&size=20
     */
    @GetMapping("/top-rated")
    public ResponseEntity<ResponseDataDTO<Page<SpaceDTO>>> getTopRatedSpaces(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get top rated spaces: page={}, size={}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Space> spaces = spaceService.getTopRatedSpaces(pageable);
        
        Page<SpaceDTO> dtos = spaces.map(this::convertToDTO);
        
        return ResponseEntity.ok(ResponseDataDTO.of(dtos, "평점 높은 공간 조회 성공"));
    }

    /**
     * 공간 생성 (Admin)
     * POST /api/v1/spaces
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDataDTO<SpaceDTO>> createSpace(@Valid @RequestBody CreateSpaceRequest request) {
        log.info("Create space request: name={}", request.getName());
        
        // TODO: CreateSpaceRequest로부터 Space entity 생성 로직 추가
        // 현재는 예제 코드
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDataDTO.of(null, "공간이 생성되었습니다"));
    }

    /**
     * 공간 수정 (Admin)
     * PUT /api/v1/spaces/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDataDTO<SpaceDTO>> updateSpace(
            @PathVariable Long id,
            @Valid @RequestBody CreateSpaceRequest request) {
        log.info("Update space request: id={}, name={}", id, request.getName());
        
        // TODO: 업데이트 로직 추가
        
        return ResponseEntity.ok(ResponseDataDTO.of(null, "공간이 수정되었습니다"));
    }

    /**
     * 공간 삭제 (Admin)
     * DELETE /api/v1/spaces/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDataDTO<Void>> deleteSpace(@PathVariable Long id) {
        log.info("Delete space request: id={}", id);
        
        spaceService.deleteSpace(id);
        
        return ResponseEntity.ok(ResponseDataDTO.of(null, "공간이 삭제되었습니다"));
    }

    /**
     * DTO 변환
     */
    private SpaceDTO convertToDTO(Space space) {
        return SpaceDTO.builder()
                .id(space.getId())
                .name(space.getName())
                .description(space.getDescription())
                .categoryName(space.getCategory().getName())
                .latitude(space.getLocation() != null ? space.getLocation().getLatitude() : 0.0)
                .longitude(space.getLocation() != null ? space.getLocation().getLongitude() : 0.0)
                .address(space.getLocation() != null ? space.getLocation().getAddress() : "")
                .capacity(space.getCapacity())
                .averageRating(space.getAverageRating())
                .imageUrl(space.getImageUrl())
                .verified(space.isVerified())
                .build();
    }
}
