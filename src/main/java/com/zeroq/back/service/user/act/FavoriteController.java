package com.zeroq.back.service.user.act;

import web.common.core.response.base.dto.ResponseDataDTO;
import com.zeroq.back.database.pub.dto.FavoriteDTO;
import com.zeroq.back.database.pub.entity.Favorite;
import com.zeroq.back.service.user.biz.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/zeroq/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    /**
     * 즐겨찾기 목록 조회
     * GET /api/v1/favorites?userId=1&page=0&size=20
     */
    @GetMapping
    public ResponseDataDTO<Page<FavoriteDTO>> getFavorites(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get favorites: userId={}, page={}, size={}", userId, page, size);

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

        return ResponseDataDTO.of(dtos, "즐겨찾기 목록 조회 성공");
    }

    /**
     * 즐겨찾기 추가
     * POST /api/v1/favorites/{spaceId}?userId=1
     */
    @PostMapping("/{spaceId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDataDTO<FavoriteDTO> addFavorite(
            @PathVariable Long spaceId,
            @RequestParam Long userId,
            @RequestParam(required = false) String note) {
        log.info("Add favorite: userId={}, spaceId={}", userId, spaceId);

        Favorite favorite = favoriteService.addFavorite(userId, spaceId, note);
        FavoriteDTO dto = FavoriteDTO.builder()
                .id(favorite.getId())
                .spaceId(favorite.getSpace().getId())
                .spaceName(favorite.getSpace().getName())
                .categoryName(favorite.getSpace().getCategory().getName())
                .order(favorite.getOrder())
                .note(favorite.getNote())
                .build();

        return ResponseDataDTO.of(dto, "즐겨찾기가 추가되었습니다");
    }

    /**
     * 즐겨찾기 제거
     * DELETE /api/v1/favorites/{spaceId}?userId=1
     */
    @DeleteMapping("/{spaceId}")
    public ResponseDataDTO<Void> removeFavorite(
            @PathVariable Long spaceId,
            @RequestParam Long userId) {
        log.info("Remove favorite: userId={}, spaceId={}", userId, spaceId);

        favoriteService.removeFavorite(userId, spaceId);

        return ResponseDataDTO.of(null, "즐겨찾기가 제거되었습니다");
    }
}
