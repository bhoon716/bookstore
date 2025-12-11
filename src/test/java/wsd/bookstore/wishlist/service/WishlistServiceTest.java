package wsd.bookstore.wishlist.service;

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
import wsd.bookstore.user.entity.User;
import wsd.bookstore.user.entity.UserRole;
import wsd.bookstore.wishlist.entity.Wishlist;
import wsd.bookstore.wishlist.repository.WishlistRepository;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {

    @InjectMocks
    private WishlistService wishlistService;

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private BookRepository bookRepository;

    @Nested
    @DisplayName("위시리스트 목록 조회 테스트")
    class GetMyWishlistTest {

        @Test
        @DisplayName("성공: 위시리스트 목록을 반환해야 한다")
        void success() {
            // given
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            ReflectionTestUtils.setField(user, "id", 1L);

            Book book = Book.builder().title("Book").build();
            ReflectionTestUtils.setField(book, "id", 1L);

            Wishlist wishlist = Wishlist.builder().user(user).book(book).build();

            given(wishlistRepository.findAllByUser(user)).willReturn(List.of(wishlist));

            // when
            List<BookSummaryResponse> result = wishlistService.getMyWishlist(user);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).isEqualTo("Book");
        }
    }

    @Nested
    @DisplayName("위시리스트 추가 테스트")
    class AddWishlistTest {

        @Test
        @DisplayName("성공: 위시리스트를 성공적으로 추가해야 한다")
        void success() {
            // given
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            ReflectionTestUtils.setField(user, "id", 1L);
            Long bookId = 1L;
            Book book = Book.builder().title("Book").build();
            ReflectionTestUtils.setField(book, "id", bookId);

            given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
            given(wishlistRepository.existsByUserAndBook(user, book)).willReturn(false);

            // when
            wishlistService.addWishlist(user, bookId);

            // then
            verify(wishlistRepository).save(any(Wishlist.class));
        }

        @Test
        @DisplayName("실패: 존재하지 않는 도서를 위시리스트에 추가하려고 하면 예외가 발생해야 한다")
        void fail_notFoundBook() {
            // given
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            Long bookId = 1L;

            given(bookRepository.findById(bookId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> wishlistService.addWishlist(user, bookId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_BOOK);
        }

        @Test
        @DisplayName("실패: 이미 위시리스트에 있는 도서를 추가하려고 하면 예외가 발생해야 한다")
        void fail_duplicateWishlist() {
            // given
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            Long bookId = 1L;
            Book book = Book.builder().title("Book").build();

            given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
            given(wishlistRepository.existsByUserAndBook(user, book)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> wishlistService.addWishlist(user, bookId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_WISHLIST);
        }
    }

    @Nested
    @DisplayName("위시리스트 삭제 테스트")
    class DeleteWishlistTest {

        @Test
        @DisplayName("성공: 위시리스트를 성공적으로 삭제해야 한다")
        void success() {
            // given
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            Long bookId = 1L;
            Book book = Book.builder().title("Book").build();
            Wishlist wishlist = Wishlist.builder().user(user).book(book).build();

            given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
            given(wishlistRepository.findByUserAndBook(user, book)).willReturn(Optional.of(wishlist));

            // when
            wishlistService.deleteWishlist(user, bookId);

            // then
            verify(wishlistRepository).delete(wishlist);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 도서의 위시리스트를 삭제하려고 하면 예외가 발생해야 한다")
        void fail_notFoundBook() {
            // given
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            Long bookId = 1L;

            given(bookRepository.findById(bookId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> wishlistService.deleteWishlist(user, bookId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_BOOK);
        }

        @Test
        @DisplayName("실패: 위시리스트에 없는 도서를 삭제하려고 하면 예외가 발생해야 한다")
        void fail_notFoundWishlist() {
            // given
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            Long bookId = 1L;
            Book book = Book.builder().title("Book").build();

            given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
            given(wishlistRepository.findByUserAndBook(user, book)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> wishlistService.deleteWishlist(user, bookId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_WISHLIST);
        }
    }
}
