package com.zeroq.back.service.user.biz;

import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.admin.entity.AdminSpace;
import com.zeroq.back.database.admin.repository.AdminSpaceRepository;
import com.zeroq.back.database.pub.dto.FavoriteDTO;
import com.zeroq.back.database.pub.entity.Favorite;
import com.zeroq.back.database.pub.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final AdminSpaceRepository adminSpaceRepository;

    /**
     * 즐겨찾기 목록 조회
     */
    public Page<Favorite> getFavorites(Long profileId, Pageable pageable) {
        return favoriteRepository.findUserFavorites(profileId, pageable);
    }

    /**
     * 즐겨찾기 추가
     */
    @Transactional
    public Favorite addFavorite(Long profileId, Long spaceId, String note) {
        AdminSpace space = findSpace(spaceId);

        // 이미 즐겨찾기된 경우
        if (favoriteRepository.findByProfileIdAndSpaceId(profileId, spaceId).isPresent()) {
            throw new LiveSpaceException.ConflictException("이미 즐겨찾기된 공간입니다");
        }

        long favoritesCount = favoriteRepository.countByProfileId(profileId);
        Favorite favorite = Favorite.builder()
                .profileId(profileId)
                .spaceId(space.getId())
                .order((int) (favoritesCount + 1))
                .note(note)
                .build();

        return favoriteRepository.save(favorite);
    }

    /**
     * 즐겨찾기 제거
     */
    @Transactional
    public void removeFavorite(Long profileId, Long spaceId) {
        Favorite favorite = favoriteRepository.findByProfileIdAndSpaceId(profileId, spaceId)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("Favorite", "spaceId", spaceId));

        favoriteRepository.delete(favorite);
        log.info("Favorite removed: profileId={}, spaceId={}", profileId, spaceId);
    }

    /**
     * 즐겨찾기 순서 변경
     */
    @Transactional
    public void reorderFavorites(Long profileId, Long favoriteId, int newOrder) {
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("Favorite", "id", favoriteId));

        if (!favorite.getProfileId().equals(profileId)) {
            throw new LiveSpaceException.ForbiddenException("다른 사용자의 즐겨찾기를 수정할 수 없습니다");
        }

        favorite.setOrder(newOrder);
        favoriteRepository.save(favorite);
    }

    public FavoriteDTO toDTO(Favorite favorite) {
        AdminSpace space = findSpace(favorite.getSpaceId());
        return FavoriteDTO.builder()
                .id(favorite.getId())
                .spaceId(space.getId())
                .spaceName(space.getName())
                .order(favorite.getOrder())
                .note(favorite.getNote())
                .build();
    }

    private AdminSpace findSpace(Long spaceId) {
        return adminSpaceRepository.findById(spaceId)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("Space", "id", spaceId));
    }
}
