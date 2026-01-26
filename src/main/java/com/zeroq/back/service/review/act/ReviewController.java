package com.zeroq.back.service.review.act;

import com.zeroq.core.response.base.dto.ResponseDataDTO;
import com.zeroq.back.database.pub.dto.ReviewDTO;
import com.zeroq.back.database.pub.entity.Review;
import com.zeroq.back.service.review.biz.ReviewService;
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
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    /**
     * 공간별 리뷰 조회
     * GET /api/v1/reviews/spaces/{spaceId}?page=0&size=20
     */
    @GetMapping("/spaces/{spaceId}")
    public ResponseEntity<ResponseDataDTO<Page<ReviewDTO>>> getReviewsBySpace(
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
                .userId(r.getUser().getId())
                .userName(r.getUser().getName())
                .rating(r.getRating())
                .title(r.getTitle())
                .content(r.getContent())
                .likeCount(r.getLikeCount())
                .verified(r.isVerified())
                .createdAt(r.getCreatedAt())
                .build());
        
        return ResponseEntity.ok(ResponseDataDTO.of(dtos, "리뷰 목록 조회 성공"));
    }

    /**
     * 사용자별 리뷰 조회
     * GET /api/v1/reviews/users/{userId}?page=0&size=20
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseDataDTO<Page<ReviewDTO>>> getReviewsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get reviews by user: userId={}, page={}, size={}", userId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews = reviewService.getReviewsByUser(userId, pageable);
        
        Page<ReviewDTO> dtos = reviews.map(r -> ReviewDTO.builder()
                .id(r.getId())
                .spaceId(r.getSpace().getId())
                .spaceName(r.getSpace().getName())
                .userId(r.getUser().getId())
                .userName(r.getUser().getName())
                .rating(r.getRating())
                .title(r.getTitle())
                .content(r.getContent())
                .likeCount(r.getLikeCount())
                .verified(r.isVerified())
                .createdAt(r.getCreatedAt())
                .build());
        
        return ResponseEntity.ok(ResponseDataDTO.of(dtos, "사용자 리뷰 조회 성공"));
    }

    /**
     * 리뷰 작성
     * POST /api/v1/reviews/spaces/{spaceId}
     */
    @PostMapping("/spaces/{spaceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDataDTO<ReviewDTO>> createReview(
            @PathVariable Long spaceId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam int rating) {
        log.info("Create review: spaceId={}, rating={}", spaceId, rating);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getDetails();
        
        Review review = reviewService.createReview(spaceId, userId, title, content, rating);
        ReviewDTO dto = ReviewDTO.builder()
                .id(review.getId())
                .spaceId(review.getSpace().getId())
                .spaceName(review.getSpace().getName())
                .userId(review.getUser().getId())
                .userName(review.getUser().getName())
                .rating(review.getRating())
                .title(review.getTitle())
                .content(review.getContent())
                .likeCount(review.getLikeCount())
                .verified(review.isVerified())
                .createdAt(review.getCreatedAt())
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDataDTO.of(dto, "리뷰가 작성되었습니다"));
    }

    /**
     * 리뷰 삭제
     * DELETE /api/v1/reviews/{reviewId}
     */
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDataDTO<Void>> deleteReview(@PathVariable Long reviewId) {
        log.info("Delete review: reviewId={}", reviewId);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getDetails();
        
        reviewService.deleteReview(reviewId, userId);
        
        return ResponseEntity.ok(ResponseDataDTO.of(null, "리뷰가 삭제되었습니다"));
    }

    /**
     * 공간 평균 평점 조회
     * GET /api/v1/reviews/spaces/{spaceId}/rating
     */
    @GetMapping("/spaces/{spaceId}/rating")
    public ResponseEntity<ResponseDataDTO<Double>> getAverageRating(@PathVariable Long spaceId) {
        log.info("Get average rating: spaceId={}", spaceId);
        
        Double averageRating = reviewService.getAverageRating(spaceId);
        
        return ResponseEntity.ok(ResponseDataDTO.of(averageRating, "평균 평점 조회 성공"));
    }
}
