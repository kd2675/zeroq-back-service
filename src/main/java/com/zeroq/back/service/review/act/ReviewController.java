package com.zeroq.back.service.review.act;

import auth.common.core.context.UserContext;
import web.common.core.response.base.dto.ResponseDataDTO;
import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.pub.dto.ReviewDTO;
import com.zeroq.back.database.pub.entity.Review;
import com.zeroq.back.service.profile.biz.ProfileUserService;
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
    private final ProfileUserService profileUserService;

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
        Page<ReviewDTO> dtos = reviews.map(reviewService::toDTO);

        return ResponseDataDTO.of(dtos, "리뷰 목록 조회 성공");
    }

    /**
     * 프로필별 리뷰 조회
     * GET /api/v1/reviews/profiles/{profileId}?page=0&size=20
     */
    @GetMapping("/profiles/{profileId}")
    public ResponseDataDTO<Page<ReviewDTO>> getReviewsByProfile(
            @PathVariable Long profileId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get reviews by profile: profileId={}, page={}, size={}", profileId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews = reviewService.getReviewsByProfile(profileId, pageable);
        Page<ReviewDTO> dtos = reviews.map(reviewService::toDTO);

        return ResponseDataDTO.of(dtos, "사용자 리뷰 조회 성공");
    }

    /**
     * Legacy 호환: userKey 경로를 profileId 경로로 매핑
     * GET /api/v1/reviews/users/{userKey}?page=0&size=20
     */
    @GetMapping("/users/{userKey}")
    public ResponseDataDTO<Page<ReviewDTO>> getReviewsByUserKey(
            @PathVariable String userKey,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long profileId = profileUserService.getProfileIdByUserKey(userKey);
        return getReviewsByProfile(profileId, page, size);
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
        Long profileId = resolveProfileId(userContext);
        log.info("Create review: profileId={}, spaceId={}, rating={}", profileId, spaceId, rating);

        Review review = reviewService.createReview(spaceId, profileId, title, content, rating);
        ReviewDTO dto = reviewService.toDTO(review);

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
        Long profileId = resolveProfileId(userContext);
        log.info("Delete review: reviewId={}, profileId={}", reviewId, profileId);

        reviewService.deleteReview(reviewId, profileId);

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
