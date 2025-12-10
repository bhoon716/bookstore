package wsd.bookstore.cart.service;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wsd.bookstore.cart.entity.Cart;
import wsd.bookstore.cart.repository.CartRepository;
import wsd.bookstore.cart.response.CartItemResponse;
import wsd.bookstore.cart.response.CartResponse;
import wsd.bookstore.user.entity.User;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;

    public CartResponse getMyCart(User user) {
        Cart cart = cartRepository.findByUser(user).orElse(null);

        if (cart == null) {
            return CartResponse.from(null, Collections.emptyList());
        }

        List<CartItemResponse> cartItems = cartRepository.findCartItems(user.getId());
        return CartResponse.from(cart, cartItems);
    }
}
