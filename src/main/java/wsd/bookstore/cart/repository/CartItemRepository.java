package wsd.bookstore.cart.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import wsd.bookstore.book.entity.Book;
import wsd.bookstore.cart.entity.Cart;
import wsd.bookstore.cart.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartAndBook(Cart cart, Book book);

    List<CartItem> findAllByCart(Cart cart);
}
