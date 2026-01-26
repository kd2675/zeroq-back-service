package com.zeroq.back.service.user.act;

import com.zeroq.core.response.base.dto.ResponseDataDTO;
import com.zeroq.back.database.pub.dto.FavoriteDTO;
import com.zeroq.back.database.pub.entity.Favorite;
import com.zeroq.back.service.user.biz.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    /**
     * 즐겨찾기 목록 조회
     * GET /api/v1/favorites?page=0&size=20
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDataDTO<Page<FavoriteDTO>>> getFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get favorites: page={}, size={}", page, size);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getDetails();
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Favorite> favorites = favoriteService.getFavorites(userId, pageable);
        
        Page<FavoriteDTO> dtos = favorites.map(f -> FavoriteDTO.builder()
                .id(f.getId())
                .spaceId(f.getSpace().getId())
                .spaceName(f.getSpace().getName())
                .categoryName(f.getSpace().getCategory().getName())
                .order(f.getOrder())
                .note(f.getNote())
                .build());
        
        return ResponseEntity.ok(ResponseDataDTO.of(dtos, "즐겨찾기 목록 조회 성공"));
    }

    /**
     * 즐겨찾기 추가
     * POST /api/v1/favorites/{spaceId}
     */
    @PostMapping("/{spaceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDataDTO<FavoriteDTO>> addFavorite(
            @PathVariable Long spaceId,
            @RequestParam(required = false) String note) {
        log.info("Add favorite: spaceId={}", spaceId);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getDetails();
        
        Favorite favorite = favoriteService.addFavorite(userId, spaceId, note);
        FavoriteDTO dto = FavoriteDTO.builder()
                .id(favorite.getId())
                .spaceId(favorite.getSpace().getId())
                .spaceName(favorite.getSpace().getName())
                .categoryName(favorite.getSpace().getCategory().getName())
                .order(favorite.getOrder())
                .note(favorite.getNote())
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDataDTO.of(dto, "즐겨찾기가 추가되었습니다"));
    }

    /**
     * 즐겨찾기 제거
     * DELETE /api/v1/favorites/{spaceId}
     */
    @DeleteMapping("/{spaceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDataDTO<Void>> removeFavorite(@PathVariable Long spaceId) {
        log.info("Remove favorite: spaceId={}", spaceId);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getDetails();
        
        favoriteService.removeFavorite(userId, spaceId);
        
        return ResponseEntity.ok(ResponseDataDTO.of(null, "즐겨찾기가 제거되었습니다"));
    }
}
