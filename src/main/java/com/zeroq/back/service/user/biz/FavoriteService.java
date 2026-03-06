package com.zeroq.back.service.user.biz;

import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.pub.entity.Space;
import com.zeroq.back.database.pub.repository.SpaceRepository;
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
    private final SpaceRepository spaceRepository;

    /**
     * 즐겨찾기 목록 조회
     */
    public Page<Favorite> getFavorites(String userKey, Pageable pageable) {
        return favoriteRepository.findUserFavorites(userKey, pageable);
    }

    /**
     * 즐겨찾기 추가
     */
    @Transactional
    public Favorite addFavorite(String userKey, Long spaceId, String note) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("Space", "id", spaceId));

        // 이미 즐겨찾기된 경우
        if (favoriteRepository.findByUserKeyAndSpaceId(userKey, spaceId).isPresent()) {
            throw new LiveSpaceException.ConflictException("이미 즐겨찾기된 공간입니다");
        }

        long favoritesCount = favoriteRepository.countByUserKey(userKey);
        Favorite favorite = Favorite.builder()
                .userKey(userKey)
                .space(space)
                .order((int) (favoritesCount + 1))
                .note(note)
                .build();

        return favoriteRepository.save(favorite);
    }

    /**
     * 즐겨찾기 제거
     */
    @Transactional
    public void removeFavorite(String userKey, Long spaceId) {
        Favorite favorite = favoriteRepository.findByUserKeyAndSpaceId(userKey, spaceId)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("Favorite", "spaceId", spaceId));

        favoriteRepository.delete(favorite);
        log.info("Favorite removed: userKey={}, spaceId={}", userKey, spaceId);
    }

    /**
     * 즐겨찾기 순서 변경
     */
    @Transactional
    public void reorderFavorites(String userKey, Long favoriteId, int newOrder) {
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("Favorite", "id", favoriteId));

        if (!favorite.getUserKey().equals(userKey)) {
            throw new LiveSpaceException.ForbiddenException("다른 사용자의 즐겨찾기를 수정할 수 없습니다");
        }

        favorite.setOrder(newOrder);
        favoriteRepository.save(favorite);
    }
}
