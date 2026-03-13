package com.zeroq.back.service.review.biz;

import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.admin.entity.AdminSpace;
import com.zeroq.back.database.admin.repository.AdminSpaceRepository;
import com.zeroq.back.database.pub.dto.ReviewDTO;
import com.zeroq.back.database.pub.entity.Review;
import com.zeroq.back.database.pub.repository.ReviewRepository;
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
    private final AdminSpaceRepository adminSpaceRepository;

    /**
     * 공간별 리뷰 조회
     */
    public Page<Review> getReviewsBySpace(Long spaceId, Pageable pageable) {
        return reviewRepository.findBySpaceIdAndDeletedFalseOrderByCreateDateDesc(spaceId, pageable);
    }

    /**
     * 사용자별 리뷰 조회
     */
    public Page<Review> getReviewsByProfile(Long profileId, Pageable pageable) {
        return reviewRepository.findByProfileIdAndDeletedFalseOrderByCreateDateDesc(profileId, pageable);
    }

    /**
     * 리뷰 작성
     */
    @Transactional
    public Review createReview(Long spaceId, Long profileId, String title, String content, int rating) {
        AdminSpace space = findSpace(spaceId);

        Review review = Review.builder()
                .spaceId(space.getId())
                .profileId(profileId)
                .title(title)
                .content(content)
                .rating(rating)
                .build();

        Review savedReview = reviewRepository.save(review);

        // 공간 평점 업데이트
        updateSpaceRating(spaceId);

        log.info("Review created: spaceId={}, profileId={}", spaceId, profileId);
        return savedReview;
    }

    /**
     * 리뷰 삭제
     */
    @Transactional
    public void deleteReview(Long reviewId, Long profileId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("Review", "id", reviewId));

        if (!review.getProfileId().equals(profileId)) {
            throw new LiveSpaceException.ForbiddenException("자신의 리뷰만 삭제할 수 있습니다");
        }

        review.setDeleted(true);
        reviewRepository.save(review);
        
        // 공간 평점 업데이트
        updateSpaceRating(review.getSpaceId());
        
        log.info("Review deleted: reviewId={}", reviewId);
    }

    /**
     * 공간 평점 업데이트
     */
    @Transactional
    public void updateSpaceRating(Long spaceId) {
        Double averageRating = reviewRepository.getAverageRating(spaceId);
        AdminSpace space = findSpace(spaceId);

        space.setAverageRating(averageRating != null ? averageRating : 0.0);
        adminSpaceRepository.save(space);
    }

    /**
     * 평균 평점 조회
     */
    public Double getAverageRating(Long spaceId) {
        Double average = reviewRepository.getAverageRating(spaceId);
        return average != null ? average : 0.0;
    }

    public ReviewDTO toDTO(Review review) {
        AdminSpace space = findSpace(review.getSpaceId());
        return ReviewDTO.builder()
                .id(review.getId())
                .spaceId(space.getId())
                .spaceName(space.getName())
                .profileId(review.getProfileId())
                .userName("")
                .rating(review.getRating())
                .title(review.getTitle())
                .content(review.getContent())
                .likeCount(review.getLikeCount())
                .verified(review.isVerified())
                .createdAt(review.getCreatedAt())
                .build();
    }

    private AdminSpace findSpace(Long spaceId) {
        return adminSpaceRepository.findById(spaceId)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("Space", "id", spaceId));
    }
}
