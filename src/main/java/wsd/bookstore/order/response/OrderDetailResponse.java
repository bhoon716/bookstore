package wsd.bookstore.order.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import wsd.bookstore.order.entity.Order;
import wsd.bookstore.order.entity.OrderStatus;

@Getter
@AllArgsConstructor
public class OrderDetailResponse {

    private Long id;
    private Long totalPrice;
    private OrderStatus status;
    private LocalDateTime orderedAt;
    private List<OrderItemResponse> orderItems;

    public static OrderDetailResponse from(Order order) {
        return new OrderDetailResponse(
                order.getId(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getOrderItems().stream()
                        .map(OrderItemResponse::from)
                        .toList());
    }
}
