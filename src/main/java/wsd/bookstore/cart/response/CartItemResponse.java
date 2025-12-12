package wsd.bookstore.cart.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;
import wsd.bookstore.cart.entity.CartItem;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Schema(description = "장바구니 항목 응답 DTO")
public class CartItemResponse {

    @Schema(description = "도서 ID", example = "1")
    private Long id;

    @Schema(description = "도서 제목", example = "Clean Code")
    private String title;

    @Schema(description = "도서 단가", example = "30000")
    private long unitPrice;

    @Schema(description = "주문 수량", example = "2")
    private Integer quantity;

    @Schema(description = "장바구니 수정 일시", example = "2025-03-10T12:00:00")
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
