package wsd.bookstore.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wsd.bookstore.book.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
}
