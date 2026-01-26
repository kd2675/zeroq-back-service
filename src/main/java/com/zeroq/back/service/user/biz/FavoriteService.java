package com.zeroq.back.service.user.biz;

import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.pub.entity.Space;
import com.zeroq.back.database.pub.repository.SpaceRepository;
import com.zeroq.back.database.pub.entity.Favorite;
import com.zeroq.back.database.pub.entity.User;
import com.zeroq.back.database.pub.repository.FavoriteRepository;
import com.zeroq.back.database.pub.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final SpaceRepository spaceRepository;

    /**
     * 즐겨찾기 목록 조회
     */
    public Page<Favorite> getFavorites(Long userId, Pageable pageable) {
        return favoriteRepository.findUserFavorites(userId, pageable);
    }

    /**
     * 즐겨찾기 추가
     */
    @Transactional
    public Favorite addFavorite(Long userId, Long spaceId, String note) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("User", "id", userId));

        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("Space", "id", spaceId));

        // 이미 즐겨찾기된 경우
        if (favoriteRepository.findByUserIdAndSpaceId(userId, spaceId).isPresent()) {
            throw new LiveSpaceException.ConflictException("이미 즐겨찾기된 공간입니다");
        }

        long favoritesCount = favoriteRepository.countByUserId(userId);
        Favorite favorite = Favorite.builder()
                .user(user)
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
    public void removeFavorite(Long userId, Long spaceId) {
        Favorite favorite = favoriteRepository.findByUserIdAndSpaceId(userId, spaceId)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("Favorite", "spaceId", spaceId));

        favoriteRepository.delete(favorite);
        log.info("Favorite removed: userId={}, spaceId={}", userId, spaceId);
    }

    /**
     * 즐겨찾기 순서 변경
     */
    @Transactional
    public void reorderFavorites(Long userId, Long favoriteId, int newOrder) {
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("Favorite", "id", favoriteId));

        if (!favorite.getUser().getId().equals(userId)) {
            throw new LiveSpaceException.ForbiddenException("다른 사용자의 즐겨찾기를 수정할 수 없습니다");
        }

        favorite.setOrder(newOrder);
        favoriteRepository.save(favorite);
    }
}
