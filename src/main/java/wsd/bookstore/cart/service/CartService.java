package wsd.bookstore.cart.service;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;

    public CartResponse getMyCart(User user) {
        log.info("장바구니 조회 요청: userId={}", user.getId());
        Cart cart = cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE).orElse(null);

        if (cart == null) {
            return CartResponse.from(null, Collections.emptyList());
        }

        List<CartItemResponse> cartItems = cartRepository.findCartItems(user.getId());
        return CartResponse.from(cart, cartItems);
    }

    @Transactional
    public void addCartItem(AddCartItemRequest request, User user) {
        log.info("장바구니 담기 요청: bookId={}, quantity={}, userId={}", request.getBookId(), request.getQuantity(),
                user.getId());
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOOK));

        Cart cart = cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseGet(() -> {
                    log.info("새 장바구니 생성: userId={}", user.getId());
                    return cartRepository.save(Cart.builder()
                            .user(user)
                            .status(CartStatus.ACTIVE)
                            .build());
                });

        CartItem cartItem = cartItemRepository.findByCartAndBook(cart, book)
                .orElse(null);

        if (cartItem != null) {
            cartItem.updateQuantity(cartItem.getQuantity() + request.getQuantity());
            log.info("기존 장바구니 상품 수량 증가: cartItemId={}, addedQuantity={}", cartItem.getId(), request.getQuantity());
            return;
        }

        CartItem savedCartItem = cartItemRepository.save(CartItem.builder()
                .cart(cart)
                .book(book)
                .quantity(request.getQuantity())
                .build());
        log.info("장바구니 상품 추가 완료: cartItemId={}", savedCartItem.getId());
    }

    @Transactional
    public CartResponse updateCartItem(Long cartItemId, UpdateCartItemRequest request, User user) {
        log.info("장바구니 상품 수량 변경 요청: cartItemId={}, quantity={}", cartItemId, request.getQuantity());
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CART_ITEM));

        Cart cart = cartItem.getCart();
        if (!cart.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (cart.getStatus() != CartStatus.ACTIVE) {
            throw new CustomException(ErrorCode.INVALID_CART_STATUS);
        }

        cartItem.updateQuantity(request.getQuantity());

        log.info("장바구니 상품 수량 변경 완료: cartItemId={}", cartItemId);
        return getMyCart(user);
    }

    @Transactional
    public void removeCartItem(Long cartItemId, User user) {
        log.info("장바구니 상품 삭제 요청: cartItemId={}", cartItemId);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CART_ITEM));

        Cart cart = cartItem.getCart();
        if (!cart.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (cart.getStatus() != CartStatus.ACTIVE) {
            throw new CustomException(ErrorCode.INVALID_CART_STATUS);
        }

        cart.getItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        log.info("장바구니 상품 삭제 완료: cartItemId={}", cartItemId);
    }

    @Transactional
    public void clearCart(User user) {
        log.info("장바구니 비우기 요청: userId={}", user.getId());
        Cart cart = cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CART_ITEM));

        cart.getItems().clear();
        log.info("장바구니 비우기 완료: cartId={}", cart.getId());
    }
}
