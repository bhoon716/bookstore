package wsd.bookstore.review.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    public Page<ReviewResponse> getReviews(Long bookId, Pageable pageable) {
        if (!bookRepository.existsById(bookId)) {
            throw new CustomException(ErrorCode.NOT_FOUND_BOOK);
        }
        return reviewRepository.getReviews(bookId, pageable);
    }

    @Transactional
    public Long createReview(Long userId, Long bookId, CreateReviewRequest request) {
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
        return savedReview.getId();
    }

    public Page<MyReviewResponse> getMyReviews(Long userId, Pageable pageable) {
        return reviewRepository.findMyReviews(userId, pageable);
    }

    @Transactional
    public void updateReview(Long userId, Long reviewId, UpdateReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

        if (!review.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        review.update(request.getRating(), request.getContent());
        review.update(request.getRating(), request.getContent());
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

        if (!review.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        reviewRepository.delete(review);
    }

    @Transactional
    public void likeReview(Long userId, Long reviewId) {
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
    }

    @Transactional
    public void unlikeReview(Long userId, Long reviewId) {
        ReviewLike reviewLike = reviewLikeRepository.findByUserIdAndReviewId(userId, reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_LIKE));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

        reviewLikeRepository.delete(reviewLike);
        review.decreaseLikeCount();
    }
}
