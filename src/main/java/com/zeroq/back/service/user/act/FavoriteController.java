package com.zeroq.back.service.user.act;

import auth.common.core.context.UserContext;
import web.common.core.response.base.dto.ResponseDataDTO;
import com.zeroq.back.common.exception.LiveSpaceException;
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
     * GET /api/v1/favorites?page=0&size=20
     */
    @GetMapping
    public ResponseDataDTO<Page<FavoriteDTO>> getFavorites(
            UserContext userContext,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String userKey = requireUserKey(userContext);
        log.info("Get favorites: userKey={}, page={}, size={}", userKey, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Favorite> favorites = favoriteService.getFavorites(userKey, pageable);

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
     * POST /api/v1/favorites/{spaceId}
     */
    @PostMapping("/{spaceId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDataDTO<FavoriteDTO> addFavorite(
            @PathVariable Long spaceId,
            UserContext userContext,
            @RequestParam(required = false) String note) {
        String userKey = requireUserKey(userContext);
        log.info("Add favorite: userKey={}, spaceId={}", userKey, spaceId);

        Favorite favorite = favoriteService.addFavorite(userKey, spaceId, note);
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
     * DELETE /api/v1/favorites/{spaceId}
     */
    @DeleteMapping("/{spaceId}")
    public ResponseDataDTO<Void> removeFavorite(
            @PathVariable Long spaceId,
            UserContext userContext) {
        String userKey = requireUserKey(userContext);
        log.info("Remove favorite: userKey={}, spaceId={}", userKey, spaceId);

        favoriteService.removeFavorite(userKey, spaceId);

        return ResponseDataDTO.of(null, "즐겨찾기가 제거되었습니다");
    }

    private String requireUserKey(UserContext userContext) {
        if (userContext == null || userContext.getUserKey() == null) {
            throw new LiveSpaceException.ForbiddenException("인증 사용자 정보가 없습니다");
        }
        return userContext.getUserKey();
    }
}
