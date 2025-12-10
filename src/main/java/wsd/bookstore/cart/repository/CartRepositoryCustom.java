package wsd.bookstore.cart.repository;

import java.util.List;
import wsd.bookstore.cart.response.CartItemResponse;

public interface CartRepositoryCustom {

    List<CartItemResponse> findCartItems(Long userId);
}
