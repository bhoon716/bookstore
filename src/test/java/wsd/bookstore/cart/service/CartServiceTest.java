package wsd.bookstore.cart.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import wsd.bookstore.book.entity.Book;
import wsd.bookstore.book.repository.BookRepository;
import wsd.bookstore.cart.entity.Cart;
import wsd.bookstore.cart.entity.CartItem;
import wsd.bookstore.cart.entity.CartStatus;
import wsd.bookstore.cart.repository.CartItemRepository;
import wsd.bookstore.cart.repository.CartRepository;
import wsd.bookstore.cart.request.AddCartItemRequest;
import wsd.bookstore.cart.request.UpdateCartItemRequest;
import wsd.bookstore.cart.response.CartItemResponse;
import wsd.bookstore.cart.response.CartResponse;
import wsd.bookstore.common.error.CustomException;
import wsd.bookstore.common.error.ErrorCode;
import wsd.bookstore.user.entity.User;
import wsd.bookstore.user.entity.UserRole;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private BookRepository bookRepository;

    @Nested
    @DisplayName("장바구니 조회 테스트")
    class GetMyCartTest {

        @Test
        @DisplayName("성공: 장바구니가 없으면 빈 장바구니를 반환한다")
        void success_noCart() {
            // given
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();

            given(cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE)).willReturn(Optional.empty());

            // when
            CartResponse response = cartService.getMyCart(user);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getItems()).isEmpty();
        }

        @Test
        @DisplayName("성공: 장바구니와 아이템이 있으면 목록을 반환한다")
        void success_withItems() {
            // given
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            ReflectionTestUtils.setField(user, "id", 1L);
            Cart cart = Cart.builder().user(user).status(CartStatus.ACTIVE).build();
            ReflectionTestUtils.setField(cart, "id", 1L);

            CartItemResponse itemResponse = new CartItemResponse(1L, "Title", 10000L, 2, java.time.LocalDateTime.now());

            given(cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE)).willReturn(Optional.of(cart));
            given(cartRepository.findCartItems(user.getId())).willReturn(List.of(itemResponse));

            // when
            CartResponse response = cartService.getMyCart(user);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getItems()).hasSize(1);
            assertThat(response.getItems().get(0).getTitle()).isEqualTo("Title");
        }
    }

    @Nested
    @DisplayName("장바구니 담기 테스트")
    class AddCartItemTest {

        @Test
        @DisplayName("성공: 새 장바구니를 생성하고 아이템을 추가한다")
        void success_newCart() {
            // given
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            ReflectionTestUtils.setField(user, "id", 1L);

            AddCartItemRequest request = new AddCartItemRequest();
            ReflectionTestUtils.setField(request, "bookId", 1L);
            ReflectionTestUtils.setField(request, "quantity", 2);

            Book book = Book.builder().title("Book").stockQuantity(100).price(1000L).build();
            ReflectionTestUtils.setField(book, "id", 1L);

            Cart cart = Cart.builder().user(user).status(CartStatus.ACTIVE).build();

            given(bookRepository.findById(1L)).willReturn(Optional.of(book));
            given(cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE)).willReturn(Optional.empty());
            given(cartRepository.save(any(Cart.class))).willReturn(cart);
            given(cartItemRepository.findByCartAndBook(cart, book)).willReturn(Optional.empty());

            CartItem savedCartItem = CartItem.builder().cart(cart).book(book).quantity(2).build();
            ReflectionTestUtils.setField(savedCartItem, "id", 1L);
            given(cartItemRepository.save(any(CartItem.class))).willReturn(savedCartItem);

            // when
            cartService.addCartItem(request, user);

            // then
            verify(cartItemRepository).save(any(CartItem.class));
        }

        @Test
        @DisplayName("성공: 기존 장바구니에 아이템이 있으면 수량을 증가시킨다")
        void success_existingItem() {
            // given
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            ReflectionTestUtils.setField(user, "id", 1L);

            AddCartItemRequest request = new AddCartItemRequest();
            ReflectionTestUtils.setField(request, "bookId", 1L);
            ReflectionTestUtils.setField(request, "quantity", 2);

            Book book = Book.builder().title("Book").stockQuantity(100).price(1000L).build();
            ReflectionTestUtils.setField(book, "id", 1L);

            Cart cart = Cart.builder().user(user).status(CartStatus.ACTIVE).build();
            CartItem cartItem = CartItem.builder().cart(cart).book(book).quantity(1).build();
            ReflectionTestUtils.setField(cartItem, "id", 1L);

            given(bookRepository.findById(1L)).willReturn(Optional.of(book));
            given(cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE)).willReturn(Optional.of(cart));
            given(cartItemRepository.findByCartAndBook(cart, book)).willReturn(Optional.of(cartItem));

            // when
            cartService.addCartItem(request, user);

            // then
            assertThat(cartItem.getQuantity()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("장바구니 아이템 수량 변경 테스트")
    class UpdateCartItemTest {

        @Test
        @DisplayName("성공: 아이템 수량을 변경한다")
        void success() {
            // given
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            ReflectionTestUtils.setField(user, "id", 1L);

            Cart cart = Cart.builder().user(user).status(CartStatus.ACTIVE).build();
            ReflectionTestUtils.setField(cart, "id", 1L);

            CartItem cartItem = CartItem.builder().cart(cart).quantity(1).build();
            ReflectionTestUtils.setField(cartItem, "id", 1L);

            UpdateCartItemRequest request = new UpdateCartItemRequest();
            ReflectionTestUtils.setField(request, "quantity", 5);

            given(cartItemRepository.findById(1L)).willReturn(Optional.of(cartItem));

            // For getMyCart call inside updateCartItem
            given(cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE)).willReturn(Optional.of(cart));
            given(cartRepository.findCartItems(user.getId())).willReturn(Collections.emptyList());

            // when
            cartService.updateCartItem(1L, request, user);

            // then
            assertThat(cartItem.getQuantity()).isEqualTo(5);
        }

        @Test
        @DisplayName("실패: 권한이 없는 사용자가 수정 시 예외가 발생한다")
        void fail_forbidden() {
            // given
            User owner = User.builder().email("owner@test.com").role(UserRole.USER).build();
            ReflectionTestUtils.setField(owner, "id", 1L);
            User otherUser = User.builder().email("other@test.com").role(UserRole.USER).build();
            ReflectionTestUtils.setField(otherUser, "id", 2L);

            Cart cart = Cart.builder().user(owner).status(CartStatus.ACTIVE).build();
            CartItem cartItem = CartItem.builder().cart(cart).quantity(1).build();

            UpdateCartItemRequest request = new UpdateCartItemRequest();
            ReflectionTestUtils.setField(request, "quantity", 5);

            given(cartItemRepository.findById(1L)).willReturn(Optional.of(cartItem));

            // when & then
            assertThatThrownBy(() -> cartService.updateCartItem(1L, request, otherUser))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN);
        }
    }

    @Nested
    @DisplayName("장바구니 아이템 삭제 테스트")
    class RemoveCartItemTest {

        @Test
        @DisplayName("성공: 아이템을 장바구니에서 삭제한다")
        void success() {
            // given
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            ReflectionTestUtils.setField(user, "id", 1L);

            Cart cart = Cart.builder().user(user).status(CartStatus.ACTIVE).build();
            CartItem cartItem = CartItem.builder().cart(cart).quantity(1).build();
            cart.getItems().add(cartItem);

            given(cartItemRepository.findById(1L)).willReturn(Optional.of(cartItem));

            // when
            cartService.removeCartItem(1L, user);

            // then
            verify(cartItemRepository).delete(cartItem);
            assertThat(cart.getItems()).doesNotContain(cartItem);
        }
    }

    @Nested
    @DisplayName("장바구니 비우기 테스트")
    class ClearCartTest {

        @Test
        @DisplayName("성공: 장바구니의 모든 아이템을 삭제한다")
        void success() {
            // given
            User user = User.builder().email("test@test.com").role(UserRole.USER).build();
            ReflectionTestUtils.setField(user, "id", 1L);

            Cart cart = Cart.builder().user(user).status(CartStatus.ACTIVE).build();
            CartItem cartItem1 = CartItem.builder().cart(cart).quantity(1).build();
            CartItem cartItem2 = CartItem.builder().cart(cart).quantity(2).build();
            cart.getItems().add(cartItem1);
            cart.getItems().add(cartItem2);

            given(cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE)).willReturn(Optional.of(cart));

            // when
            cartService.clearCart(user);

            // then
            assertThat(cart.getItems()).isEmpty();
        }
    }
}
