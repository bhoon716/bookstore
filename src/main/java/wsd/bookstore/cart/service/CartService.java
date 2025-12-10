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
import wsd.bookstore.cart.repository.CartItemRepository;
import wsd.bookstore.cart.repository.CartRepository;
import wsd.bookstore.cart.request.AddCartItemRequest;
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
        Cart cart = cartRepository.findByUser(user).orElse(null);

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

        Cart cart = cartRepository.findByUser(user)
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
}
