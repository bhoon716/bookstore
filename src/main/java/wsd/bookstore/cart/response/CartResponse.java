package wsd.bookstore.cart.response;

import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import wsd.bookstore.cart.entity.Cart;

@Getter
@Builder
public class CartResponse {

    private Long id;
    private int totalCount;
    private long totalPrice;
    private List<CartItemResponse> items;

    public static CartResponse from(Cart cart, List<CartItemResponse> cartItems) {
        if (cart == null) {
            return CartResponse.builder()
                    .id(null)
                    .totalCount(0)
                    .totalPrice(0)
                    .items(Collections.emptyList())
                    .build();
        }

        int totalCount = cartItems.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        long totalPrice = cartItems.stream()
                .mapToLong(item -> item.getUnitPrice() * item.getQuantity())
                .sum();

        return CartResponse.builder()
                .id(cart.getId())
                .totalCount(totalCount)
                .totalPrice(totalPrice)
                .items(cartItems)
                .build();
    }
}
