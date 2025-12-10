package wsd.bookstore.cart.service;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;

    public CartResponse getMyCart(User user) {
        Cart cart = cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE).orElse(null);

        if (cart == null) {
            return CartResponse.from(null, Collections.emptyList());
        }

        List<CartItemResponse> cartItems = cartRepository.findCartItems(user.getId());
        return CartResponse.from(cart, cartItems);
    }

    @Transactional
    public void addCartItem(AddCartItemRequest request, User user) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOOK));

        Cart cart = cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));

        CartItem cartItem = cartItemRepository.findByCartAndBook(cart, book)
                .orElse(null);

        if (cartItem != null) {
            cartItem.updateQuantity(cartItem.getQuantity() + request.getQuantity());
            return;
        }

        cartItemRepository.save(CartItem.builder()
                .cart(cart)
                .book(book)
                .quantity(request.getQuantity())
                .build());
    }

    @Transactional
    public CartResponse updateCartItem(Long cartItemId, UpdateCartItemRequest request, User user) {
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

        return getMyCart(user);
    }

    @Transactional
    public void removeCartItem(Long cartItemId, User user) {
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
    }

    @Transactional
    public void clearCart(User user) {
        Cart cart = cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CART_ITEM));

        cart.getItems().clear();
    }
}
