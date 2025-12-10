package wsd.bookstore.cart.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import wsd.bookstore.cart.entity.Cart;
import wsd.bookstore.cart.entity.CartStatus;
import wsd.bookstore.user.entity.User;

public interface CartRepository extends JpaRepository<Cart, Long>, CartRepositoryCustom {

    Optional<Cart> findByUserAndStatus(User user, CartStatus status);
}
