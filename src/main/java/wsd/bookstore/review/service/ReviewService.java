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

    public Page<ReviewResponse> getReviews(Long bookId, Pageable pageable) {
        if (!bookRepository.existsById(bookId)) {
            throw new CustomException(ErrorCode.NOT_FOUND_BOOK);
        }
        return reviewRepository.getReviews(bookId, pageable);
    }

    @Transactional
    public void createReview(Long userId, Long bookId, CreateReviewRequest request) {
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

        reviewRepository.save(review);
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
    }
}
