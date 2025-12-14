package wsd.bookstore.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wsd.bookstore.book.entity.Book;
import wsd.bookstore.book.repository.BookRepository;
import wsd.bookstore.common.error.CustomException;
import wsd.bookstore.common.error.ErrorCode;
import wsd.bookstore.review.entity.Review;
import wsd.bookstore.review.entity.ReviewLike;
import wsd.bookstore.review.repository.ReviewLikeRepository;
import wsd.bookstore.review.repository.ReviewRepository;
import wsd.bookstore.review.request.CreateReviewRequest;
import wsd.bookstore.review.request.UpdateReviewRequest;
import wsd.bookstore.review.response.MyReviewResponse;
import wsd.bookstore.review.response.ReviewResponse;
import wsd.bookstore.user.entity.User;
import wsd.bookstore.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    public Page<ReviewResponse> getReviews(Long bookId, Pageable pageable) {
        log.info("리뷰 목록 조회 요청: bookId={}", bookId);
        if (!bookRepository.existsById(bookId)) {
            throw new CustomException(ErrorCode.NOT_FOUND_BOOK);
        }
        return reviewRepository.getReviews(bookId, pageable);
    }

    public Page<MyReviewResponse> getMyReviews(Long userId, Pageable pageable) {
        log.info("내 리뷰 목록 조회 요청: userId={}", userId);
        return reviewRepository.findMyReviews(userId, pageable);
    }

    @Transactional
    public Long createReview(Long userId, Long bookId, CreateReviewRequest request) {
        log.info("리뷰 작성 요청: userId={}, bookId={}", userId, bookId);
        if (reviewRepository.existsByUserIdAndBookId(userId, bookId)) {
            throw new CustomException(ErrorCode.DUPLICATE_REVIEW);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOOK));

        Review review = Review.builder()
                .user(user)
                .book(book)
                .rating(request.getRating())
                .body(request.getContent())
                .build();

        Review savedReview = reviewRepository.save(review);
        log.info("리뷰 작성 완료: reviewId={}", savedReview.getId());
        return savedReview.getId();
    }

    @Transactional
    public void updateReview(Long userId, Long reviewId, UpdateReviewRequest request) {
        log.info("리뷰 수정 요청: reviewId={}, userId={}", reviewId, userId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

        if (!review.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        review.update(request.getRating(), request.getContent());
        log.info("리뷰 수정 완료: reviewId={}", reviewId);
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        log.info("리뷰 삭제 요청: reviewId={}, userId={}", reviewId, userId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

        if (!review.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        reviewRepository.delete(review);
        log.info("리뷰 삭제 완료: reviewId={}", reviewId);
    }

    @Transactional
    public void likeReview(Long userId, Long reviewId) {
        log.info("리뷰 좋아요 요청: reviewId={}, userId={}", reviewId, userId);
        if (reviewLikeRepository.existsByUserIdAndReviewId(userId, reviewId)) {
            throw new CustomException(ErrorCode.ALREADY_LIKED_REVIEW);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

        ReviewLike reviewLike = ReviewLike.builder()
                .user(user)
                .review(review)
                .build();

        reviewLikeRepository.save(reviewLike);
        review.increaseLikeCount();
        log.info("리뷰 좋아요 완료: reviewId={}", reviewId);
    }

    @Transactional
    public void unlikeReview(Long userId, Long reviewId) {
        log.info("리뷰 좋아요 취소 요청: reviewId={}, userId={}", reviewId, userId);
        ReviewLike reviewLike = reviewLikeRepository.findByUserIdAndReviewId(userId, reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_LIKE));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

        reviewLikeRepository.delete(reviewLike);
        review.decreaseLikeCount();
        log.info("리뷰 좋아요 취소 완료: reviewId={}", reviewId);
    }
}
