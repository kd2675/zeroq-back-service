package com.zeroq.back.service.review.act;

import auth.common.core.context.UserContext;
import web.common.core.response.base.dto.ResponseDataDTO;
import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.pub.dto.ReviewDTO;
import com.zeroq.back.database.pub.entity.Review;
import com.zeroq.back.service.review.biz.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/zeroq/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    /**
     * 공간별 리뷰 조회
     * GET /api/v1/reviews/spaces/{spaceId}?page=0&size=20
     */
    @GetMapping("/spaces/{spaceId}")
    public ResponseDataDTO<Page<ReviewDTO>> getReviewsBySpace(
            @PathVariable Long spaceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get reviews by space: spaceId={}, page={}, size={}", spaceId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews = reviewService.getReviewsBySpace(spaceId, pageable);

        Page<ReviewDTO> dtos = reviews.map(r -> ReviewDTO.builder()
                .id(r.getId())
                .spaceId(r.getSpace().getId())
                .spaceName(r.getSpace().getName())
                .userKey(r.getUserKey())
                .userName("")
                .rating(r.getRating())
                .title(r.getTitle())
                .content(r.getContent())
                .likeCount(r.getLikeCount())
                .verified(r.isVerified())
                .createdAt(r.getCreatedAt())
                .build());

        return ResponseDataDTO.of(dtos, "리뷰 목록 조회 성공");
    }

    /**
     * 사용자별 리뷰 조회
     * GET /api/v1/reviews/users/{userKey}?page=0&size=20
     */
    @GetMapping("/users/{userKey}")
    public ResponseDataDTO<Page<ReviewDTO>> getReviewsByUser(
            @PathVariable String userKey,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get reviews by user: userKey={}, page={}, size={}", userKey, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews = reviewService.getReviewsByUser(userKey, pageable);

        Page<ReviewDTO> dtos = reviews.map(r -> ReviewDTO.builder()
                .id(r.getId())
                .spaceId(r.getSpace().getId())
                .spaceName(r.getSpace().getName())
                .userKey(r.getUserKey())
                .userName("")
                .rating(r.getRating())
                .title(r.getTitle())
                .content(r.getContent())
                .likeCount(r.getLikeCount())
                .verified(r.isVerified())
                .createdAt(r.getCreatedAt())
                .build());

        return ResponseDataDTO.of(dtos, "사용자 리뷰 조회 성공");
    }

    /**
     * 리뷰 작성
     * POST /api/v1/reviews/spaces/{spaceId}
     */
    @PostMapping("/spaces/{spaceId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDataDTO<ReviewDTO> createReview(
            @PathVariable Long spaceId,
            UserContext userContext,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam int rating) {
        String userKey = requireUserKey(userContext);
        log.info("Create review: userKey={}, spaceId={}, rating={}", userKey, spaceId, rating);

        Review review = reviewService.createReview(spaceId, userKey, title, content, rating);
        ReviewDTO dto = ReviewDTO.builder()
                .id(review.getId())
                .spaceId(review.getSpace().getId())
                .spaceName(review.getSpace().getName())
                .userKey(review.getUserKey())
                .userName("")
                .rating(review.getRating())
                .title(review.getTitle())
                .content(review.getContent())
                .likeCount(review.getLikeCount())
                .verified(review.isVerified())
                .createdAt(review.getCreatedAt())
                .build();

        return ResponseDataDTO.of(dto, "리뷰가 작성되었습니다");
    }

    /**
     * 리뷰 삭제
     * DELETE /api/v1/reviews/{reviewId}
     */
    @DeleteMapping("/{reviewId}")
    public ResponseDataDTO<Void> deleteReview(
            @PathVariable Long reviewId,
            UserContext userContext) {
        String userKey = requireUserKey(userContext);
        log.info("Delete review: reviewId={}, userKey={}", reviewId, userKey);

        reviewService.deleteReview(reviewId, userKey);

        return ResponseDataDTO.of(null, "리뷰가 삭제되었습니다");
    }

    /**
     * 공간 평균 평점 조회
     * GET /api/v1/reviews/spaces/{spaceId}/rating
     */
    @GetMapping("/spaces/{spaceId}/rating")
    public ResponseDataDTO<Double> getAverageRating(@PathVariable Long spaceId) {
        log.info("Get average rating: spaceId={}", spaceId);

        Double averageRating = reviewService.getAverageRating(spaceId);

        return ResponseDataDTO.of(averageRating, "평균 평점 조회 성공");
    }

    private String requireUserKey(UserContext userContext) {
        if (userContext == null || userContext.getUserKey() == null) {
            throw new LiveSpaceException.ForbiddenException("인증 사용자 정보가 없습니다");
        }
        return userContext.getUserKey();
    }
}
