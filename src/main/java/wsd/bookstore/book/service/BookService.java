package wsd.bookstore.book.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
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
import wsd.bookstore.book.response.BookResponse;
import wsd.bookstore.common.error.CustomException;
import wsd.bookstore.common.error.ErrorCode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final PublisherRepository publisherRepository;

    public Page<BookResponse> searchBooks(BookSearchCondition condition, Pageable pageable) {
        return bookRepository.search(condition, pageable);
    }

    @Transactional
    public Long createBook(BookCreateRequest request) {
        validateDuplicateIsbn(request.getIsbn13());

        Publisher publisher = findPublisherById(request.getPublisherId());
        Book book = buildBook(request, publisher);

        addAuthorsToBook(book, request.getAuthorIds());
        addCategoriesToBook(book, request.getCategoryIds());

        return bookRepository.save(book).getId();
    }

    @Transactional
    public Long updateBook(Long bookId, BookUpdateRequest request) {
        Book book = findBookById(bookId);

        updateBookInfo(book, request);
        updateBookPublisher(book, request.getPublisherId());
        updateBookAuthors(book, request.getAuthorIds());
        updateBookCategories(book, request.getCategoryIds());

        return book.getId();
    }

    @Transactional
    public void deleteBook(Long bookId) {
        Book book = findBookById(bookId);
        bookRepository.delete(book);
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
