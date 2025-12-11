package wsd.bookstore.order.controller;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
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
    public ResponseEntity<ApiResponse<Void>> checkout(@AuthenticationPrincipal(expression = "user") User user) {
        Long orderId = orderService.checkout(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(orderId)
                .toUri();
        return ApiResponse.created(null, location, "체크아웃 성공");
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PagedModel<OrderSummaryResponse>>> getMyOrders(
            @AuthenticationPrincipal(expression = "user") User user,
            @PageableDefault(sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<OrderSummaryResponse> orders = orderService.getMyOrders(user, pageable);
        return ApiResponse.ok(new PagedModel<>(orders), "주문 내역 조회 성공");
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getOrderDetail(
            @PathVariable Long orderId,
            @AuthenticationPrincipal(expression = "user") User user) {
        OrderDetailResponse response = orderService.getOrderDetail(orderId, user);
        return ApiResponse.ok(response, "주문 상세 조회 성공");
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal(expression = "user") User user) {
        orderService.cancelOrder(orderId, user);
        return ApiResponse.noContent("주문 취소 성공");
    }
}
