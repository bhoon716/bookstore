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

        Publisher publisher = findPublisher(request.getPublisherId());

        Book book = buildBook(request, publisher);

        attachAuthors(book, request.getAuthorIds());
        attachCategories(book, request.getCategoryIds());

        return bookRepository.save(book).getId();
    }

    private void validateDuplicateIsbn(String isbn13) {
        if (bookRepository.existsByIsbn13(isbn13)) {
            throw new CustomException(ErrorCode.DUPLICATE_BOOK, "book_isbn=" + isbn13);
        }
    }

    private Publisher findPublisher(Long publisherId) {
        return publisherRepository.findById(publisherId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.NOT_FOUND_PUBLISHER,
                        "publisher_id=" + publisherId));
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

    private void attachAuthors(Book book, List<Long> authorIds) {
        if (authorIds == null || authorIds.isEmpty()) {
            return;
        }

        List<Author> authors = authorRepository.findAllById(authorIds);
        if (authors.size() != authorIds.size()) {
            throw new CustomException(ErrorCode.NOT_FOUND_AUTHOR);
        }

        authors.forEach(book::addAuthor);
    }

    private void attachCategories(Book book, List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }

        List<Category> categories = categoryRepository.findAllById(categoryIds);
        if (categories.size() != categoryIds.size()) {
            throw new CustomException(ErrorCode.NOT_FOUND_CATEGORY);
        }

        categories.forEach(book::addCategory);
    }
}
