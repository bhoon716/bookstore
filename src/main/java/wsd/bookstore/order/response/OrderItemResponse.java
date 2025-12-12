package wsd.bookstore.order.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wsd.bookstore.order.entity.OrderItem;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@AllArgsConstructor
@Schema(description = "주문 항목 정보 응답 DTO")
public class OrderItemResponse {

    @Schema(description = "주문 항목 ID", example = "1")
    private Long id;

    @Schema(description = "도서 ID", example = "1")
    private Long bookId;

    @Schema(description = "도서 제목", example = "Clean Code")
    private String bookTitle;

    @Schema(description = "주문 수량", example = "1")
    private Integer quantity;

    @Schema(description = "주문 가격 (단가 * 수량)", example = "25000")
    private Long orderPrice;

    public static OrderItemResponse from(OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getId(),
                orderItem.getBook().getId(),
                orderItem.getBook().getTitle(),
                orderItem.getQuantity(),
                orderItem.getOrderPrice());
    }
}
