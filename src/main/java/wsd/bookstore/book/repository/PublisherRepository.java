package wsd.bookstore.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wsd.bookstore.book.entity.Publisher;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
}
