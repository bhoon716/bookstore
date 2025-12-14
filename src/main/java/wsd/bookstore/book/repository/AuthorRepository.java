package wsd.bookstore.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wsd.bookstore.book.entity.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
