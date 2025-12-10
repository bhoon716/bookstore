package wsd.bookstore.cart.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;
import wsd.bookstore.cart.entity.CartItem;

@Getter
public class CartItemResponse {

    private Long id;
    private String title;
    private long unitPrice;
    private Integer quantity;
    private LocalDateTime updatedAt;

    @QueryProjection
    public CartItemResponse(Long id, String title, long unitPrice, Integer quantity, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.updatedAt = updatedAt;
    }

    public static CartItemResponse from(CartItem cartItem) {
        return new CartItemResponse(
                cartItem.getBook().getId(),
                cartItem.getBook().getTitle(),
                cartItem.getBook().getPrice(),
                cartItem.getQuantity(),
                cartItem.getUpdatedAt());
    }
}
