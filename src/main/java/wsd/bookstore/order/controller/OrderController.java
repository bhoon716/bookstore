package wsd.bookstore.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wsd.bookstore.common.response.ApiResponse;
import wsd.bookstore.order.response.OrderDetailResponse;
import wsd.bookstore.order.response.OrderSummaryResponse;
import wsd.bookstore.order.service.OrderService;
import wsd.bookstore.user.entity.User;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<Long>> checkout(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(orderService.checkout(user), "체크아웃 성공"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PagedModel<OrderSummaryResponse>>> getMyOrders(
            @AuthenticationPrincipal User user,
            @PageableDefault(sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<OrderSummaryResponse> orders = orderService.getMyOrders(user, pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedModel<>(orders), "주문 내역 조회 성공"));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getOrderDetail(
            @PathVariable Long orderId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderDetail(orderId, user), "주문 상세 조회 성공"));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal User user) {
        orderService.cancelOrder(orderId, user);
        return ResponseEntity.ok(ApiResponse.noContent("주문 취소 성공"));
    }
}
