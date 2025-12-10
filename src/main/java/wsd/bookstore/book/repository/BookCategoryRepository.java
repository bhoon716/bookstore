package wsd.bookstore.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wsd.bookstore.book.entity.BookCategory;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {
}
