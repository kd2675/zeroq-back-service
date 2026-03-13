package com.zeroq.back.service.user.act;

import auth.common.core.context.UserContext;
import web.common.core.response.base.dto.ResponseDataDTO;
import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.pub.dto.FavoriteDTO;
import com.zeroq.back.database.pub.entity.Favorite;
import com.zeroq.back.service.profile.biz.ProfileUserService;
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
    private final ProfileUserService profileUserService;

    /**
     * 즐겨찾기 목록 조회
     * GET /api/v1/favorites?page=0&size=20
     */
    @GetMapping
    public ResponseDataDTO<Page<FavoriteDTO>> getFavorites(
            UserContext userContext,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long profileId = resolveProfileId(userContext);
        log.info("Get favorites: profileId={}, page={}, size={}", profileId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Favorite> favorites = favoriteService.getFavorites(profileId, pageable);
        Page<FavoriteDTO> dtos = favorites.map(favoriteService::toDTO);

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
        Long profileId = resolveProfileId(userContext);
        log.info("Add favorite: profileId={}, spaceId={}", profileId, spaceId);

        Favorite favorite = favoriteService.addFavorite(profileId, spaceId, note);
        FavoriteDTO dto = favoriteService.toDTO(favorite);

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
        Long profileId = resolveProfileId(userContext);
        log.info("Remove favorite: profileId={}, spaceId={}", profileId, spaceId);

        favoriteService.removeFavorite(profileId, spaceId);

        return ResponseDataDTO.of(null, "즐겨찾기가 제거되었습니다");
    }

    private Long resolveProfileId(UserContext userContext) {
        if (userContext == null || !userContext.isAuthenticated()) {
            throw new LiveSpaceException.UnauthorizedException("Login required");
        }
        if (!userContext.isUser()) {
            throw new LiveSpaceException.ForbiddenException("USER role required");
        }
        if (userContext.getUserKey() == null || userContext.getUserKey().isBlank()) {
            throw new LiveSpaceException.ForbiddenException("인증 사용자 정보가 없습니다");
        }
        return profileUserService.resolveProfileId(userContext.getUserKey(), userContext.getUserName());
    }
}
