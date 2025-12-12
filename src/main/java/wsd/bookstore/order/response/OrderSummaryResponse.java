package wsd.bookstore.order.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import wsd.bookstore.order.entity.Order;
import wsd.bookstore.order.entity.OrderItem;
import wsd.bookstore.order.entity.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@AllArgsConstructor
@Schema(description = "주문 요약 정보 응답 DTO")
public class OrderSummaryResponse {

    @Schema(description = "주문 ID", example = "501")
    private Long id;

    @Schema(description = "총 주문 금액 (원)", example = "25000")
    private Long totalPrice;

    @Schema(description = "주문 상태 (CREATED/PAID/DELIVERED/CANCELLED)", example = "CREATED")
    private OrderStatus status;

    @Schema(description = "주문 일시", example = "2025-03-10T11:20:00")
    private LocalDateTime orderedAt;

    @Schema(description = "대표 주문 도서명", example = "Clean Code 외 0권")
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
