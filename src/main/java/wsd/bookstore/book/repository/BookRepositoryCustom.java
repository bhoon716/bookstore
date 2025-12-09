package wsd.bookstore.book.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import wsd.bookstore.book.request.BookSearchCondition;
import wsd.bookstore.book.response.BookResponse;

public interface BookRepositoryCustom {

    Page<BookResponse> search(BookSearchCondition condition, Pageable pageable);
}
