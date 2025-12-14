package wsd.bookstore.book.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import wsd.bookstore.book.request.BookSearchCondition;
import wsd.bookstore.book.response.BookSummaryResponse;

public interface BookRepositoryCustom {

    Page<BookSummaryResponse> search(BookSearchCondition condition, Pageable pageable);

    List<BookSummaryResponse> findBestSellers(int limit);
}
