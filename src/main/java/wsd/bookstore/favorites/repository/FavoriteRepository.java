package wsd.bookstore.favorites.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import wsd.bookstore.book.entity.Book;
import wsd.bookstore.favorites.entity.Favorite;
import wsd.bookstore.user.entity.User;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserAndBook(User user, Book book);

    Optional<Favorite> findByUserAndBook(User user, Book book);

    List<Favorite> findAllByUser(User user);
}
