package wsd.bookstore.book.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final PublisherRepository publisherRepository;

    public Page<BookSummaryResponse> searchBooks(BookSearchCondition condition, Pageable pageable) {
        log.info("도서 검색 요청");
        return bookRepository.search(condition, pageable);
    }

    public BookDetailResponse getBookDetail(Long bookId) {
        log.info("도서 상세 조회 요청: id={}", bookId);
        Book book = findBookById(bookId);
        return BookDetailResponse.from(book);
    }

    @org.springframework.cache.annotation.Cacheable(value = "bestSellers", key = "'top10'")
    public List<BookSummaryResponse> getBestSellers() {
        log.info("베스트셀러 조회 요청");
        return bookRepository.findBestSellers(10);
    }

    @Transactional
    public BookSummaryResponse createBook(BookCreateRequest request) {
        log.info("도서 생성 요청: isbn={}", request.getIsbn13());
        validateDuplicateIsbn(request.getIsbn13());

        Publisher publisher = findPublisherById(request.getPublisherId());
        Book book = buildBook(request, publisher);

        addAuthorsToBook(book, request.getAuthorIds());
        addCategoriesToBook(book, request.getCategoryIds());

        Book savedBook = bookRepository.save(book);
        log.info("도서 생성 완료: id={}", savedBook.getId());
        return BookSummaryResponse.from(savedBook);
    }

    @Transactional
    public BookSummaryResponse updateBook(Long bookId, BookUpdateRequest request) {
        log.info("도서 수정 요청: id={}", bookId);
        Book book = findBookById(bookId);

        updateBookInfo(book, request);
        updateBookPublisher(book, request.getPublisherId());
        updateBookAuthors(book, request.getAuthorIds());
        updateBookCategories(book, request.getCategoryIds());

        log.info("도서 수정 완료: id={}", bookId);
        return BookSummaryResponse.from(book);
    }

    @Transactional
    public void deleteBook(Long bookId) {
        log.info("도서 삭제 요청: id={}", bookId);
        Book book = findBookById(bookId);
        bookRepository.delete(book);
        log.info("도서 삭제 완료: id={}", bookId);
    }

    private Book findBookById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOOK, "book_id=" + bookId));
    }

    private void validateDuplicateIsbn(String isbn13) {
        if (bookRepository.existsByIsbn13(isbn13)) {
            throw new CustomException(ErrorCode.DUPLICATE_BOOK, "book_isbn=" + isbn13);
        }
    }

    private Book buildBook(BookCreateRequest request, Publisher publisher) {
        return Book.builder()
                .isbn13(request.getIsbn13())
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .publishedAt(request.getPublishedAt())
                .publisher(publisher)
                .build();
    }

    private void updateBookInfo(Book book, BookUpdateRequest request) {
        book.updateBasicInfo(
                request.getTitle(),
                request.getDescription(),
                request.getPrice(),
                request.getStockQuantity(),
                request.getPublishedAt());
    }

    private void updateBookPublisher(Book book, Long publisherId) {
        Publisher publisher = findPublisherById(publisherId);
        book.updatePublisher(publisher);
    }

    private void updateBookAuthors(Book book, List<Long> authorIds) {
        book.clearAuthors();
        addAuthorsToBook(book, authorIds);
    }

    private void updateBookCategories(Book book, List<Long> categoryIds) {
        book.clearCategories();
        addCategoriesToBook(book, categoryIds);
    }

    private Publisher findPublisherById(Long publisherId) {
        return publisherRepository.findById(publisherId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.NOT_FOUND_PUBLISHER,
                        "publisher_id=" + publisherId));
    }

    private void addAuthorsToBook(Book book, List<Long> authorIds) {
        if (authorIds == null || authorIds.isEmpty()) {
            return;
        }

        List<Author> authors = findAuthorsByIds(authorIds);
        authors.forEach(book::addAuthor);
    }

    private List<Author> findAuthorsByIds(List<Long> authorIds) {
        List<Author> authors = authorRepository.findAllById(authorIds);
        if (authors.size() != authorIds.size()) {
            throw new CustomException(ErrorCode.NOT_FOUND_AUTHOR);
        }
        return authors;
    }

    private void addCategoriesToBook(Book book, List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }

        List<Category> categories = findCategoriesByIds(categoryIds);
        categories.forEach(book::addCategory);
    }

    private List<Category> findCategoriesByIds(List<Long> categoryIds) {
        List<Category> categories = categoryRepository.findAllById(categoryIds);
        if (categories.size() != categoryIds.size()) {
            throw new CustomException(ErrorCode.NOT_FOUND_CATEGORY);
        }
        return categories;
    }
}
