package wsd.bookstore.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wsd.bookstore.common.response.ApiResponse;
import wsd.bookstore.order.service.OrderService;
import wsd.bookstore.security.auth.CustomUserDetails;
import wsd.bookstore.user.entity.User;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<Long>> checkout(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        Long orderId = orderService.checkout(user);
        return ResponseEntity.ok(ApiResponse.success(orderId, "주문이 완료되었습니다."));
    }
}
