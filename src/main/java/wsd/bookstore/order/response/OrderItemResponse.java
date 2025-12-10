package wsd.bookstore.order.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wsd.bookstore.order.entity.OrderItem;

@Getter
@AllArgsConstructor
public class OrderItemResponse {

    private Long id;
    private Long bookId;
    private String bookTitle;
    private Integer quantity;
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
