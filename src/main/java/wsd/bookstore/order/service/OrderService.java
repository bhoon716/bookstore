package wsd.bookstore.order.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wsd.bookstore.cart.entity.Cart;
import wsd.bookstore.cart.entity.CartItem;
import wsd.bookstore.cart.entity.CartStatus;
import wsd.bookstore.cart.repository.CartRepository;
import wsd.bookstore.common.error.CustomException;
import wsd.bookstore.common.error.ErrorCode;
import wsd.bookstore.order.entity.Order;
import wsd.bookstore.order.entity.OrderItem;
import wsd.bookstore.order.entity.OrderStatus;
import wsd.bookstore.order.repository.OrderRepository;
import wsd.bookstore.order.response.OrderDetailResponse;
import wsd.bookstore.order.response.OrderSummaryResponse;
import wsd.bookstore.user.entity.User;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    public Page<OrderSummaryResponse> getMyOrders(User user, Pageable pageable) {
        log.info("주문 목록 조회 요청: userId={}", user.getId());
        return orderRepository.findAllByUser_Id(user.getId(), pageable)
                .map(OrderSummaryResponse::from);
    }

    public OrderDetailResponse getOrderDetail(Long orderId, User user) {
        log.info("주문 상세 조회 요청: orderId={}, userId={}", orderId, user.getId());
        Order order = orderRepository.findByIdAndUser_Id(orderId, user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ORDER));
        return OrderDetailResponse.from(order);
    }

    @Transactional
    public Long checkout(User user) {
        log.info("주문 생성 요청: userId={}", user.getId());
        Cart cart = cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CART_ITEM));

        List<CartItem> cartItems = cart.getItems();
        if (cartItems.isEmpty()) {
            log.warn("장바구니가 비어있음: userId={}", user.getId());
            throw new CustomException(ErrorCode.NOT_FOUND_CART_ITEM);
        }

        return processCheckout(cart, user);
    }

    @Transactional
    public void cancelOrder(Long orderId, User user) {
        log.info("주문 취소 요청: orderId={}, userId={}", orderId, user.getId());
        Order order = orderRepository.findByIdAndUser_Id(orderId, user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ORDER));

        order.cancel();
        log.info("주문 취소 완료: orderId={}", orderId);
    }

    private Long processCheckout(Cart cart, User user) {
        long totalPrice = cart.getItems().stream()
                .mapToLong(item -> item.getBook().getPrice() * item.getQuantity())
                .sum();

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.COMPLETED)
                .totalPrice(totalPrice)
                .build();

        cart.getItems().stream()
                .map(item -> {
                    item.getBook().decreaseStock(item.getQuantity());
                    return OrderItem.builder()
                            .book(item.getBook())
                            .quantity(item.getQuantity())
                            .orderPrice(item.getBook().getPrice())
                            .build();
                })
                .forEach(order::addOrderItem);

        orderRepository.save(order);

        cart.updateStatus(CartStatus.ORDERED);

        log.info("주문 생성 완료: orderId={}, totalPrice={}", order.getId(), totalPrice);
        return order.getId();
    }
}
