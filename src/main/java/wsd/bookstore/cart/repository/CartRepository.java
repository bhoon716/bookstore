package wsd.bookstore.cart.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import wsd.bookstore.cart.entity.Cart;
import wsd.bookstore.user.entity.User;

public interface CartRepository extends JpaRepository<Cart, Long>, CartRepositoryCustom {

    Optional<Cart> findByUser(User user);
}
