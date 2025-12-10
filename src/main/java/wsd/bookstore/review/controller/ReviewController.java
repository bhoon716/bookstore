package wsd.bookstore.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wsd.bookstore.common.response.ApiResponse;
import wsd.bookstore.review.request.CreateReviewRequest;
import wsd.bookstore.review.request.UpdateReviewRequest;
import wsd.bookstore.review.response.MyReviewResponse;
import wsd.bookstore.review.response.ReviewResponse;
import wsd.bookstore.review.service.ReviewService;
import wsd.bookstore.security.auth.CustomUserDetails;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/books/{bookId}/reviews")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getReviews(
            @PathVariable Long bookId,
            Pageable pageable) {
        Page<ReviewResponse> reviews = reviewService.getReviews(bookId, pageable);
        return ResponseEntity.ok(ApiResponse.success(reviews, "도서 리뷰 목록 조회 성공"));
    }

    @PostMapping("/books/{bookId}/reviews")
    public ResponseEntity<ApiResponse<Void>> createReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long bookId,
            @RequestBody @Valid CreateReviewRequest request) {
        reviewService.createReview(userDetails.getUserId(), bookId, request);
        return ResponseEntity.ok(ApiResponse.success("리뷰 등록 성공"));
    }

    @GetMapping("/reviews/me")
    public ResponseEntity<ApiResponse<Page<MyReviewResponse>>> getMyReviews(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable) {
        Page<MyReviewResponse> reviews = reviewService.getMyReviews(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(reviews, "내가 작성한 리뷰 목록 조회 성공"));
    }

    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> updateReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long reviewId,
            @RequestBody @Valid UpdateReviewRequest request) {
        reviewService.updateReview(userDetails.getUserId(), reviewId, request);
        return ResponseEntity.ok(ApiResponse.success("리뷰 수정 성공"));
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long reviewId) {
        reviewService.deleteReview(userDetails.getUserId(), reviewId);
        return ResponseEntity.ok(ApiResponse.success("리뷰 삭제 성공"));
    }
}
