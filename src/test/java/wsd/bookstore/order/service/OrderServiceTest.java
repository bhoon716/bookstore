package wsd.bookstore.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import wsd.bookstore.book.entity.Book;
import wsd.bookstore.cart.entity.Cart;
import wsd.bookstore.cart.entity.CartItem;
import wsd.bookstore.cart.entity.CartStatus;
import wsd.bookstore.cart.repository.CartRepository;
import wsd.bookstore.common.error.CustomException;
import wsd.bookstore.common.error.ErrorCode;
import wsd.bookstore.order.entity.Order;
import wsd.bookstore.order.entity.OrderStatus;
import wsd.bookstore.order.repository.OrderRepository;
import wsd.bookstore.order.response.OrderDetailResponse;
import wsd.bookstore.order.response.OrderSummaryResponse;
import wsd.bookstore.user.entity.User;
import wsd.bookstore.user.entity.UserRole;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Nested
    @DisplayName("주문 목록 조회 테스트")
    class GetMyOrdersTest {

        @Test
        @DisplayName("성공: 주문 목록을 반환해야 한다")
        void success() {
            // given
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            ReflectionTestUtils.setField(user, "id", 1L);
            Pageable pageable = PageRequest.of(0, 10);

            Order order = Order.builder().user(user).totalPrice(10000L).status(OrderStatus.COMPLETED).build();
            ReflectionTestUtils.setField(order, "id", 1L);
            List<Order> orders = List.of(order);
            Page<Order> page = new PageImpl<>(orders, pageable, 1);

            given(orderRepository.findAllByUser_Id(user.getId(), pageable)).willReturn(page);

            // when
            Page<OrderSummaryResponse> result = orderService.getMyOrders(user, pageable);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(order.getId());
        }
    }

    @Nested
    @DisplayName("주문 상세 조회 테스트")
    class GetOrderDetailTest {

        @Test
        @DisplayName("성공: 주문 상세 정보를 반환해야 한다")
        void success() {
            // given
            Long orderId = 1L;
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            ReflectionTestUtils.setField(user, "id", 1L);

            Order order = Order.builder().user(user).totalPrice(10000L).status(OrderStatus.COMPLETED).build();
            ReflectionTestUtils.setField(order, "id", orderId);

            given(orderRepository.findByIdAndUser_Id(orderId, user.getId())).willReturn(Optional.of(order));

            // when
            OrderDetailResponse response = orderService.getOrderDetail(orderId, user);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(orderId);
            assertThat(response.getTotalPrice()).isEqualTo(10000L);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 주문 조회 시 예외가 발생해야 한다")
        void fail_notFound() {
            // given
            Long orderId = 1L;
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            ReflectionTestUtils.setField(user, "id", 1L);

            given(orderRepository.findByIdAndUser_Id(orderId, user.getId())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> orderService.getOrderDetail(orderId, user))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_ORDER);
        }
    }

    @Nested
    @DisplayName("주문 생성(결제) 테스트")
    class CheckoutTest {

        @Test
        @DisplayName("성공: 장바구니 아이템으로 주문을 생성해야 한다")
        void success() {
            // given
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            ReflectionTestUtils.setField(user, "id", 1L);

            Cart cart = Cart.builder().user(user).status(CartStatus.ACTIVE).build();

            Book book = Book.builder().title("Book").price(10000L).stockQuantity(10).build();
            ReflectionTestUtils.setField(book, "id", 1L);

            CartItem cartItem = CartItem.builder().cart(cart).book(book).quantity(2).build();
            cart.getItems().add(cartItem);

            given(cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE)).willReturn(Optional.of(cart));

            given(orderRepository.save(any(Order.class))).willAnswer(invocation -> {
                Order order = invocation.getArgument(0);
                ReflectionTestUtils.setField(order, "id", 1L);
                return order;
            });

            // when
            Long orderId = orderService.checkout(user);

            // then
            assertThat(orderId).isEqualTo(1L);
            assertThat(cart.getStatus()).isEqualTo(CartStatus.ORDERED);
            assertThat(book.getStockQuantity()).isEqualTo(8); // 10 - 2
            verify(orderRepository).save(any(Order.class));
        }

        @Test
        @DisplayName("실패: 장바구니가 비어있으면 예외가 발생해야 한다")
        void fail_emptyCart() {
            // given
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            ReflectionTestUtils.setField(user, "id", 1L);

            Cart cart = Cart.builder().user(user).status(CartStatus.ACTIVE).build();

            given(cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE)).willReturn(Optional.of(cart));

            // when & then
            assertThatThrownBy(() -> orderService.checkout(user))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_CART_ITEM);
        }
    }

    @Nested
    @DisplayName("주문 취소 테스트")
    class CancelOrderTest {

        @Test
        @DisplayName("성공: 주문을 취소 상태로 변경해야 한다")
        void success() {
            // given
            Long orderId = 1L;
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            ReflectionTestUtils.setField(user, "id", 1L);

            Order order = Order.builder().user(user).totalPrice(10000L).status(OrderStatus.COMPLETED).build();
            ReflectionTestUtils.setField(order, "id", orderId);

            given(orderRepository.findByIdAndUser_Id(orderId, user.getId())).willReturn(Optional.of(order));

            // when
            orderService.cancelOrder(orderId, user);

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 주문 취소 시 예외가 발생해야 한다")
        void fail_notFound() {
            // given
            Long orderId = 1L;
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            ReflectionTestUtils.setField(user, "id", 1L);

            given(orderRepository.findByIdAndUser_Id(orderId, user.getId())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> orderService.cancelOrder(orderId, user))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_ORDER);
        }
    }
}
