package wsd.bookstore.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wsd.bookstore.book.repository.BookRepository;
import wsd.bookstore.book.request.BookSearchCondition;
import wsd.bookstore.book.response.BookResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    public Page<BookResponse> searchBooks(BookSearchCondition condition, Pageable pageable) {
        return bookRepository.search(condition, pageable);
    }
}
