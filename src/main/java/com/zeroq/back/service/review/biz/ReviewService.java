package com.zeroq.back.service.review.biz;

import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.pub.entity.Review;
import com.zeroq.back.database.pub.repository.ReviewRepository;
import com.zeroq.back.database.pub.entity.Space;
import com.zeroq.back.database.pub.repository.SpaceRepository;
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
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final SpaceRepository spaceRepository;

    /**
     * 공간별 리뷰 조회
     */
    public Page<Review> getReviewsBySpace(Long spaceId, Pageable pageable) {
        return reviewRepository.findBySpaceIdAndDeletedFalseOrderByCreateDateDesc(spaceId, pageable);
    }

    /**
     * 사용자별 리뷰 조회
     */
    public Page<Review> getReviewsByUser(Long userId, Pageable pageable) {
        return reviewRepository.findByUserIdAndDeletedFalseOrderByCreateDateDesc(userId, pageable);
    }

    /**
     * 리뷰 작성
     */
    @Transactional
    public Review createReview(Long spaceId, Long userId, String title, String content, int rating) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("Space", "id", spaceId));

        Review review = Review.builder()
                .space(space)
                .userId(userId)
                .title(title)
                .content(content)
                .rating(rating)
                .build();

        Review savedReview = reviewRepository.save(review);

        // 공간 평점 업데이트
        updateSpaceRating(spaceId);

        log.info("Review created: spaceId={}, userId={}", spaceId, userId);
        return savedReview;
    }

    /**
     * 리뷰 삭제
     */
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("Review", "id", reviewId));

        if (!review.getUserId().equals(userId)) {
            throw new LiveSpaceException.ForbiddenException("자신의 리뷰만 삭제할 수 있습니다");
        }

        review.setDeleted(true);
        reviewRepository.save(review);
        
        // 공간 평점 업데이트
        updateSpaceRating(review.getSpace().getId());
        
        log.info("Review deleted: reviewId={}", reviewId);
    }

    /**
     * 공간 평점 업데이트
     */
    @Transactional
    public void updateSpaceRating(Long spaceId) {
        Double averageRating = reviewRepository.getAverageRating(spaceId);
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("Space", "id", spaceId));

        space.setAverageRating(averageRating != null ? averageRating : 0.0);
        spaceRepository.save(space);
    }

    /**
     * 평균 평점 조회
     */
    public Double getAverageRating(Long spaceId) {
        Double average = reviewRepository.getAverageRating(spaceId);
        return average != null ? average : 0.0;
    }
}
