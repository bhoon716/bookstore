package wsd.bookstore.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
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
import wsd.bookstore.user.entity.UserRole;
import wsd.bookstore.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewLikeRepository reviewLikeRepository;

    @Nested
    @DisplayName("리뷰 목록 조회 테스트")
    class GetReviewsTest {

        @Test
        @DisplayName("성공: 해당 도서의 리뷰 목록을 반환해야 한다")
        void success() {
            // given
            Long bookId = 1L;
            Pageable pageable = PageRequest.of(0, 10);

            // Assuming ReviewResponse is a projection or DTO
            List<ReviewResponse> content = Collections.emptyList();
            Page<ReviewResponse> page = new PageImpl<>(content, pageable, 0);

            given(bookRepository.existsById(bookId)).willReturn(true);
            given(reviewRepository.getReviews(bookId, pageable)).willReturn(page);

            // when
            Page<ReviewResponse> result = reviewService.getReviews(bookId, pageable);

            // then
            assertThat(result).isEqualTo(page);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 도서의 리뷰 조회 시 예외가 발생해야 한다")
        void fail_notFoundBook() {
            // given
            Long bookId = 1L;
            Pageable pageable = PageRequest.of(0, 10);

            given(bookRepository.existsById(bookId)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> reviewService.getReviews(bookId, pageable))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_BOOK);
        }
    }

    @Nested
    @DisplayName("내 리뷰 목록 조회 테스트")
    class GetMyReviewsTest {

        @Test
        @DisplayName("성공: 내가 작성한 리뷰 목록을 반환해야 한다")
        void success() {
            // given
            Long userId = 1L;
            Pageable pageable = PageRequest.of(0, 10);

            List<MyReviewResponse> content = Collections.emptyList();
            Page<MyReviewResponse> page = new PageImpl<>(content, pageable, 0);

            given(reviewRepository.findMyReviews(userId, pageable)).willReturn(page);

            // when
            Page<MyReviewResponse> result = reviewService.getMyReviews(userId, pageable);

            // then
            assertThat(result).isEqualTo(page);
        }
    }

    @Nested
    @DisplayName("리뷰 작성 테스트")
    class CreateReviewTest {

        @Test
        @DisplayName("성공: 리뷰를 성공적으로 작성해야 한다")
        void success() {
            // given
            Long userId = 1L;
            Long bookId = 1L;
            CreateReviewRequest request = new CreateReviewRequest(5, "Content");

            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            ReflectionTestUtils.setField(user, "id", userId);

            Book book = Book.builder().title("Book").build();
            ReflectionTestUtils.setField(book, "id", bookId);

            Review review = Review.builder().user(user).book(book).rating(5).body("Content").build();
            ReflectionTestUtils.setField(review, "id", 1L);

            given(reviewRepository.existsByUserIdAndBookId(userId, bookId)).willReturn(false);
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
            given(reviewRepository.save(any(Review.class))).willReturn(review);

            // when
            Long reviewId = reviewService.createReview(userId, bookId, request);

            // then
            assertThat(reviewId).isEqualTo(1L);
            verify(reviewRepository).save(any(Review.class));
        }

        @Test
        @DisplayName("실패: 이미 작성한 리뷰가 있으면 예외가 발생해야 한다")
        void fail_duplicateReview() {
            // given
            Long userId = 1L;
            Long bookId = 1L;
            CreateReviewRequest request = new CreateReviewRequest(5, "Content");

            given(reviewRepository.existsByUserIdAndBookId(userId, bookId)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> reviewService.createReview(userId, bookId, request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_REVIEW);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 사용자가 리뷰 작성 시 예외가 발생해야 한다")
        void fail_notFoundUser() {
            // given
            Long userId = 1L;
            Long bookId = 1L;
            CreateReviewRequest request = new CreateReviewRequest(5, "Content");

            given(reviewRepository.existsByUserIdAndBookId(userId, bookId)).willReturn(false);
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> reviewService.createReview(userId, bookId, request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_USER);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 도서에 리뷰 작성 시 예외가 발생해야 한다")
        void fail_notFoundBook() {
            // given
            Long userId = 1L;
            Long bookId = 1L;
            CreateReviewRequest request = new CreateReviewRequest(5, "Content");
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();

            given(reviewRepository.existsByUserIdAndBookId(userId, bookId)).willReturn(false);
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(bookRepository.findById(bookId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> reviewService.createReview(userId, bookId, request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_BOOK);
        }
    }

    @Nested
    @DisplayName("리뷰 수정 테스트")
    class UpdateReviewTest {

        @Test
        @DisplayName("성공: 리뷰를 성공적으로 수정해야 한다")
        void success() {
            // given
            Long userId = 1L;
            Long reviewId = 1L;
            UpdateReviewRequest request = new UpdateReviewRequest(4, "Updated Content");

            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            ReflectionTestUtils.setField(user, "id", userId);

            Review review = Review.builder().user(user).rating(5).body("Content").build();
            ReflectionTestUtils.setField(review, "id", reviewId);

            given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

            // when
            reviewService.updateReview(userId, reviewId, request);

            // then
            assertThat(review.getRating()).isEqualTo(4);
            assertThat(review.getBody()).isEqualTo("Updated Content");
        }

        @Test
        @DisplayName("실패: 다른 사용자의 리뷰를 수정하려고 하면 예외가 발생해야 한다")
        void fail_forbidden() {
            // given
            Long userId = 1L;
            Long reviewId = 1L;
            Long otherUserId = 2L;
            UpdateReviewRequest request = new UpdateReviewRequest(4, "Updated Content");

            User otherUser = User.builder().email("other@test.com").role(UserRole.USER).build();
            ReflectionTestUtils.setField(otherUser, "id", otherUserId);

            Review review = Review.builder().user(otherUser).rating(5).body("Content").build(); // Review owned by
                                                                                                // otherUser

            given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

            // when & then
            assertThatThrownBy(() -> reviewService.updateReview(userId, reviewId, request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN);
        }
    }

    @Nested
    @DisplayName("리뷰 삭제 테스트")
    class DeleteReviewTest {

        @Test
        @DisplayName("성공: 리뷰를 성공적으로 삭제해야 한다")
        void success() {
            // given
            Long userId = 1L;
            Long reviewId = 1L;

            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            ReflectionTestUtils.setField(user, "id", userId);

            Review review = Review.builder().user(user).build();
            ReflectionTestUtils.setField(review, "id", reviewId);

            given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

            // when
            reviewService.deleteReview(userId, reviewId);

            // then
            verify(reviewRepository).delete(review);
        }

        @Test
        @DisplayName("실패: 다른 사용자의 리뷰를 삭제하려고 하면 예외가 발생해야 한다")
        void fail_forbidden() {
            // given
            Long userId = 1L;
            Long reviewId = 1L;
            Long otherUserId = 2L;

            User otherUser = User.builder().email("other@test.com").role(UserRole.USER).build();
            ReflectionTestUtils.setField(otherUser, "id", otherUserId);

            Review review = Review.builder().user(otherUser).build();

            given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

            // when & then
            assertThatThrownBy(() -> reviewService.deleteReview(userId, reviewId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN);
        }
    }

    @Nested
    @DisplayName("리뷰 좋아요 테스트")
    class LikeReviewTest {

        @Test
        @DisplayName("성공: 리뷰에 좋아요를 성공적으로 눌러야 한다")
        void success() {
            // given
            Long userId = 1L;
            Long reviewId = 1L;

            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            Review review = Review.builder().user(user).build(); // owner doesn't matter for like

            given(reviewLikeRepository.existsByUserIdAndReviewId(userId, reviewId)).willReturn(false);
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

            // when
            reviewService.likeReview(userId, reviewId);

            // then
            verify(reviewLikeRepository).save(any(ReviewLike.class));
            assertThat(review.getLikeCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("실패: 이미 좋아요를 누른 리뷰에 다시 좋아요를 누르면 예외가 발생해야 한다")
        void fail_alreadyLiked() {
            // given
            Long userId = 1L;
            Long reviewId = 1L;

            given(reviewLikeRepository.existsByUserIdAndReviewId(userId, reviewId)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> reviewService.likeReview(userId, reviewId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_LIKED_REVIEW);
        }
    }

    @Nested
    @DisplayName("리뷰 좋아요 취소 테스트")
    class UnlikeReviewTest {

        @Test
        @DisplayName("성공: 리뷰 좋아요를 성공적으로 취소해야 한다")
        void success() {
            // given
            Long userId = 1L;
            Long reviewId = 1L;

            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            Review review = Review.builder().user(user).build();
            ReflectionTestUtils.setField(review, "likeCount", 1);
            ReviewLike reviewLike = ReviewLike.builder().user(user).review(review).build();

            given(reviewLikeRepository.findByUserIdAndReviewId(userId, reviewId)).willReturn(Optional.of(reviewLike));
            given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

            // when
            reviewService.unlikeReview(userId, reviewId);

            // then
            verify(reviewLikeRepository).delete(reviewLike);
            assertThat(review.getLikeCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("실패: 좋아요를 누르지 않은 상태에서 취소하면 예외가 발생해야 한다")
        void fail_notFoundLike() {
            // given
            Long userId = 1L;
            Long reviewId = 1L;

            given(reviewLikeRepository.findByUserIdAndReviewId(userId, reviewId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> reviewService.unlikeReview(userId, reviewId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_LIKE);
        }
    }
}
