package wsd.bookstore.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wsd.bookstore.common.response.ApiResponse;
import wsd.bookstore.review.request.CreateReviewRequest;
import wsd.bookstore.review.response.ReviewResponse;
import wsd.bookstore.review.service.ReviewService;
import wsd.bookstore.security.auth.CustomUserDetails;

@RestController
@RequestMapping("/api/books/{bookId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getReviews(
            @PathVariable Long bookId,
            Pageable pageable) {
        Page<ReviewResponse> reviews = reviewService.getReviews(bookId, pageable);
        return ResponseEntity.ok(ApiResponse.success(reviews, "도서 리뷰 목록 조회 성공"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long bookId,
            @RequestBody @Valid CreateReviewRequest request) {
        reviewService.createReview(userDetails.getUserId(), bookId, request);
        return ResponseEntity.ok(ApiResponse.success("리뷰 등록 성공"));
    }
}
