package wsd.bookstore.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import wsd.bookstore.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAllByUser_Id(Long userId, Pageable pageable);
}
