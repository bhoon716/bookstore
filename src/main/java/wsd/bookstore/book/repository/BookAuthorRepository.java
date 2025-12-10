package wsd.bookstore.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wsd.bookstore.book.entity.BookAuthor;

public interface BookAuthorRepository extends JpaRepository<BookAuthor, Long> {
}
