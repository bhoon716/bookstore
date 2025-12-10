package wsd.bookstore.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wsd.bookstore.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
