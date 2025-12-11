package wsd.bookstore.book.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
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
import wsd.bookstore.book.entity.Author;
import wsd.bookstore.book.entity.Book;
import wsd.bookstore.book.entity.Category;
import wsd.bookstore.book.entity.Publisher;
import wsd.bookstore.book.repository.AuthorRepository;
import wsd.bookstore.book.repository.BookRepository;
import wsd.bookstore.book.repository.CategoryRepository;
import wsd.bookstore.book.repository.PublisherRepository;
import wsd.bookstore.book.request.BookCreateRequest;
import wsd.bookstore.book.request.BookSearchCondition;
import wsd.bookstore.book.request.BookUpdateRequest;
import wsd.bookstore.book.response.BookDetailResponse;
import wsd.bookstore.book.response.BookSummaryResponse;
import wsd.bookstore.common.error.CustomException;
import wsd.bookstore.common.error.ErrorCode;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PublisherRepository publisherRepository;

    @Nested
    @DisplayName("도서 검색 테스트")
    class SearchBooksTest {

        @Test
        @DisplayName("성공: 도서 검색 결과를 반환해야 한다")
        void success() {
            // given
            BookSearchCondition condition = new BookSearchCondition("keyword", 1L, 1L, 1L);
            Pageable pageable = PageRequest.of(0, 10);
            List<BookSummaryResponse> content = List.of();
            Page<BookSummaryResponse> page = new PageImpl<>(content, pageable, 0);

            given(bookRepository.search(condition, pageable)).willReturn(page);

            // when
            Page<BookSummaryResponse> result = bookService.searchBooks(condition, pageable);

            // then
            assertThat(result).isEqualTo(page);
        }
    }

    @Nested
    @DisplayName("도서 상세 조회 테스트")
    class GetBookDetailTest {

        @Test
        @DisplayName("성공: 도서 상세 정보를 반환해야 한다")
        void success() {
            // given
            Long bookId = 1L;
            Book book = Book.builder()
                    .isbn13("9781234567890")
                    .title("Title")
                    .description("Description")
                    .price(10000L)
                    .stockQuantity(100)
                    .publishedAt(LocalDateTime.now())
                    .publisher(new Publisher("Publisher"))
                    .build();
            ReflectionTestUtils.setField(book, "id", bookId);

            given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

            // when
            BookDetailResponse response = bookService.getBookDetail(bookId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(bookId);
            assertThat(response.getTitle()).isEqualTo(book.getTitle());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 도서 조회 시 예외가 발생해야 한다")
        void fail_notFound() {
            // given
            Long bookId = 1L;
            given(bookRepository.findById(bookId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> bookService.getBookDetail(bookId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_BOOK);
        }
    }

    @Nested
    @DisplayName("도서 생성 테스트")
    class CreateBookTest {

        @Test
        @DisplayName("성공: 도서를 성공적으로 생성해야 한다")
        void success() {
            // given
            BookCreateRequest request = new BookCreateRequest(
                    "9781234567890",
                    "Title",
                    "Description",
                    10000L,
                    100,
                    LocalDateTime.now(),
                    1L,
                    List.of(1L),
                    List.of(1L));

            Publisher publisher = new Publisher("Publisher");
            Author author = new Author("Author", "Bio");
            Category category = new Category("Category");

            given(bookRepository.existsByIsbn13(request.getIsbn13())).willReturn(false);
            given(publisherRepository.findById(request.getPublisherId())).willReturn(Optional.of(publisher));
            given(authorRepository.findAllById(request.getAuthorIds())).willReturn(List.of(author));
            given(categoryRepository.findAllById(request.getCategoryIds())).willReturn(List.of(category));

            Book savedBook = Book.builder()
                    .isbn13(request.getIsbn13())
                    .title(request.getTitle())
                    .publisher(publisher)
                    .build();
            ReflectionTestUtils.setField(savedBook, "id", 1L);

            given(bookRepository.save(any(Book.class))).willReturn(savedBook);

            // when
            BookSummaryResponse response = bookService.createBook(request);

            // then
            assertThat(response).isNotNull();
            verify(bookRepository).save(any(Book.class));
        }

        @Test
        @DisplayName("실패: 중복된 ISBN으로 도서 생성 시 예외가 발생해야 한다")
        void fail_duplicateIsbn() {
            // given
            BookCreateRequest request = new BookCreateRequest(
                    "9781234567890",
                    "Title",
                    "Description",
                    10000L,
                    100,
                    LocalDateTime.now(),
                    1L,
                    List.of(1L),
                    List.of(1L));

            given(bookRepository.existsByIsbn13(request.getIsbn13())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> bookService.createBook(request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_BOOK);
        }
    }

    @Nested
    @DisplayName("도서 수정 테스트")
    class UpdateBookTest {

        @Test
        @DisplayName("성공: 도서 정보를 성공적으로 수정해야 한다")
        void success() {
            // given
            Long bookId = 1L;
            BookUpdateRequest request = new BookUpdateRequest(
                    "Updated Title",
                    "Updated Description",
                    20000L,
                    50,
                    LocalDateTime.now(),
                    2L,
                    List.of(2L),
                    List.of(2L));

            Publisher oldPublisher = new Publisher("Old Publisher");
            Book book = Book.builder()
                    .isbn13("9781234567890")
                    .title("Old Title")
                    .publisher(oldPublisher)
                    .build();
            ReflectionTestUtils.setField(book, "id", bookId);

            Publisher newPublisher = new Publisher("New Publisher");
            Author newAuthor = new Author("New Author", "Bio");
            Category newCategory = new Category("New Category");

            given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
            given(publisherRepository.findById(request.getPublisherId())).willReturn(Optional.of(newPublisher));
            given(authorRepository.findAllById(request.getAuthorIds())).willReturn(List.of(newAuthor));
            given(categoryRepository.findAllById(request.getCategoryIds())).willReturn(List.of(newCategory));

            // when
            BookSummaryResponse response = bookService.updateBook(bookId, request);

            // then
            assertThat(response.getTitle()).isEqualTo(request.getTitle());
            assertThat(book.getTitle()).isEqualTo(request.getTitle());
            assertThat(book.getPublisher()).isEqualTo(newPublisher);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 도서 수정 시 예외가 발생해야 한다")
        void fail_notFound() {
            // given
            Long bookId = 1L;
            BookUpdateRequest request = new BookUpdateRequest(
                    "Updated Title",
                    "Updated Description",
                    20000L,
                    50,
                    LocalDateTime.now(),
                    2L,
                    List.of(2L),
                    List.of(2L));

            given(bookRepository.findById(bookId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> bookService.updateBook(bookId, request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_BOOK);
        }
    }

    @Nested
    @DisplayName("도서 삭제 테스트")
    class DeleteBookTest {

        @Test
        @DisplayName("성공: 도서를 성공적으로 삭제해야 한다")
        void success() {
            // given
            Long bookId = 1L;
            Book book = Book.builder()
                    .isbn13("9781234567890")
                    .title("Title")
                    .build();
            ReflectionTestUtils.setField(book, "id", bookId);

            given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

            // when
            bookService.deleteBook(bookId);

            // then
            verify(bookRepository).delete(book);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 도서 삭제 시 예외가 발생해야 한다")
        void fail_notFound() {
            // given
            Long bookId = 1L;
            given(bookRepository.findById(bookId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> bookService.deleteBook(bookId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_BOOK);
        }
    }
}
