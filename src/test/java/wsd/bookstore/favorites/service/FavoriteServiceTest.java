package wsd.bookstore.favorites.service;

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
import org.springframework.test.util.ReflectionTestUtils;
import wsd.bookstore.book.entity.Book;
import wsd.bookstore.book.repository.BookRepository;
import wsd.bookstore.book.response.BookSummaryResponse;
import wsd.bookstore.common.error.CustomException;
import wsd.bookstore.common.error.ErrorCode;
import wsd.bookstore.favorites.entity.Favorite;
import wsd.bookstore.favorites.repository.FavoriteRepository;
import wsd.bookstore.user.entity.User;
import wsd.bookstore.user.entity.UserRole;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @InjectMocks
    private FavoriteService favoriteService;

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private BookRepository bookRepository;

    @Nested
    @DisplayName("좋아요 목록 조회 테스트")
    class GetMyFavoritesTest {

        @Test
        @DisplayName("성공: 좋아요한 도서 목록을 반환해야 한다")
        void success() {
            // given
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            ReflectionTestUtils.setField(user, "id", 1L);

            Book book = Book.builder().title("Book").build();
            ReflectionTestUtils.setField(book, "id", 1L);

            Favorite favorite = Favorite.builder().user(user).book(book).build();

            given(favoriteRepository.findAllByUser(user)).willReturn(List.of(favorite));

            // when
            List<BookSummaryResponse> result = favoriteService.getMyFavorites(user);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).isEqualTo("Book");
        }
    }

    @Nested
    @DisplayName("좋아요 추가 테스트")
    class AddFavoriteTest {

        @Test
        @DisplayName("성공: 좋아요를 성공적으로 추가해야 한다")
        void success() {
            // given
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            ReflectionTestUtils.setField(user, "id", 1L);
            Long bookId = 1L;
            Book book = Book.builder().title("Book").build();
            ReflectionTestUtils.setField(book, "id", bookId);

            given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
            given(favoriteRepository.existsByUserAndBook(user, book)).willReturn(false);

            // when
            favoriteService.addFavorite(user, bookId);

            // then
            verify(favoriteRepository).save(any(Favorite.class));
        }

        @Test
        @DisplayName("실패: 존재하지 않는 도서를 좋아요하려고 하면 예외가 발생해야 한다")
        void fail_notFoundBook() {
            // given
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            Long bookId = 1L;

            given(bookRepository.findById(bookId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoriteService.addFavorite(user, bookId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_BOOK);
        }

        @Test
        @DisplayName("실패: 이미 좋아요한 도서를 다시 좋아요하려고 하면 예외가 발생해야 한다")
        void fail_duplicateFavorite() {
            // given
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            Long bookId = 1L;
            Book book = Book.builder().title("Book").build();

            given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
            given(favoriteRepository.existsByUserAndBook(user, book)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> favoriteService.addFavorite(user, bookId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_FAVORITE);
        }
    }

    @Nested
    @DisplayName("좋아요 취소 테스트")
    class DeleteFavoriteTest {

        @Test
        @DisplayName("성공: 좋아요를 성공적으로 취소해야 한다")
        void success() {
            // given
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            Long bookId = 1L;
            Book book = Book.builder().title("Book").build();
            Favorite favorite = Favorite.builder().user(user).book(book).build();

            given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
            given(favoriteRepository.findByUserAndBook(user, book)).willReturn(Optional.of(favorite));

            // when
            favoriteService.deleteFavorite(user, bookId);

            // then
            verify(favoriteRepository).delete(favorite);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 도서의 좋아요를 취소하려고 하면 예외가 발생해야 한다")
        void fail_notFoundBook() {
            // given
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            Long bookId = 1L;

            given(bookRepository.findById(bookId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoriteService.deleteFavorite(user, bookId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_BOOK);
        }

        @Test
        @DisplayName("실패: 좋아요하지 않은 도서의 좋아요를 취소하려고 하면 예외가 발생해야 한다")
        void fail_notFoundFavorite() {
            // given
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            Long bookId = 1L;
            Book book = Book.builder().title("Book").build();

            given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
            given(favoriteRepository.findByUserAndBook(user, book)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoriteService.deleteFavorite(user, bookId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_FAVORITE);
        }
    }
}
