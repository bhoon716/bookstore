package wsd.bookstore.order.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import wsd.bookstore.order.entity.Order;
import wsd.bookstore.order.entity.OrderItem;
import wsd.bookstore.order.entity.OrderStatus;

@Getter
@AllArgsConstructor
public class OrderSummaryResponse {

    private Long id;
    private Long totalPrice;
    private OrderStatus status;
    private LocalDateTime orderedAt;
    private String representativeBookTitle;

    public static OrderSummaryResponse from(Order order) {
        return new OrderSummaryResponse(
                order.getId(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getCreatedAt(),
                createRepresentativeBookTitle(order.getOrderItems()));
    }

    private static String createRepresentativeBookTitle(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return "";
        }

        String firstBookTitle = orderItems.getFirst().getBook().getTitle();
        int otherItemsCount = orderItems.size() - 1;

        if (otherItemsCount > 0) {
            return String.format("%s 외 %d권", firstBookTitle, otherItemsCount);
        }
        return firstBookTitle;
    }
}
