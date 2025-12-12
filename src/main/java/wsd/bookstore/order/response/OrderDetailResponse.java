package wsd.bookstore.order.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import wsd.bookstore.order.entity.Order;
import wsd.bookstore.order.entity.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@AllArgsConstructor
@Schema(description = "주문 상세 정보 응답 DTO")
public class OrderDetailResponse {

    @Schema(description = "주문 ID", example = "501")
    private Long id;

    @Schema(description = "총 주문 금액 (원)", example = "25000")
    private Long totalPrice;

    @Schema(description = "주문 상태", example = "CREATED")
    private OrderStatus status;

    @Schema(description = "주문 일시", example = "2025-03-10T11:20:00")
    private LocalDateTime orderedAt;

    @Schema(description = "주문 상세 항목 리스트")
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
