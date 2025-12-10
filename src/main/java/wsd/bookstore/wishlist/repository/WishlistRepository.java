package wsd.bookstore.wishlist.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import wsd.bookstore.book.entity.Book;
import wsd.bookstore.user.entity.User;
import wsd.bookstore.wishlist.entity.Wishlist;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    boolean existsByUserAndBook(User user, Book book);

    Optional<Wishlist> findByUserAndBook(User user, Book book);

    List<Wishlist> findAllByUser(User user);
}
