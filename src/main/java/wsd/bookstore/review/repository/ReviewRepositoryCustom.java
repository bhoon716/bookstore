package wsd.bookstore.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import wsd.bookstore.review.response.MyReviewResponse;
import wsd.bookstore.review.response.ReviewResponse;

public interface ReviewRepositoryCustom {

    Page<ReviewResponse> getReviews(Long bookId, Pageable pageable);

    Page<MyReviewResponse> findMyReviews(Long userId, Pageable pageable);
}
