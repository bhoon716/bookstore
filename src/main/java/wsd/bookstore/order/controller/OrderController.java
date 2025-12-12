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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "주문 API")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    @Operation(summary = "주문 생성", description = "장바구니 항목으로 주문을 생성합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "주문 생성 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "주문 생성 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "체크아웃 성공",
                "payload": null
            }
            """)))
    public ResponseEntity<ApiResponse<Void>> checkout(@AuthenticationPrincipal(expression = "user") User user) {
        Long orderId = orderService.checkout(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(orderId)
                .toUri();
        return ApiResponse.created(null, location, "체크아웃 성공");
    }

    @GetMapping("/me")
    @Operation(summary = "내 주문 내역 조회", description = "내 주문 내역을 페이징 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "내 주문 조회 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "주문 내역 조회 성공",
                "payload": {
                    "content": [
                        {
                            "id": 501,
                            "totalPrice": 25000,
                            "status": "CREATED",
                            "orderedAt": "2025-03-10T11:20:00",
                            "representativeBookTitle": "Clean Code 외 0권"
                        }
                    ],
                    "page": {
                        "size": 20,
                        "number": 0,
                        "totalElements": 1,
                        "totalPages": 1
                    }
                }
            }
            """)))
    public ResponseEntity<ApiResponse<PagedModel<OrderSummaryResponse>>> getMyOrders(
            @AuthenticationPrincipal(expression = "user") User user,
            @PageableDefault(sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<OrderSummaryResponse> orders = orderService.getMyOrders(user, pageable);
        return ApiResponse.ok(new PagedModel<>(orders), "주문 내역 조회 성공");
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "주문 상세 조회", description = "주문 상세 정보를 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "주문 상세 조회 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "주문 상세 조회 성공",
                "payload": {
                    "id": 501,
                    "totalPrice": 25000,
                    "status": "CREATED",
                    "orderedAt": "2025-03-10T11:20:00",
                    "orderItems": [
                         {
                            "id": 1,
                            "bookId": 1,
                            "bookTitle": "Clean Code",
                            "quantity": 1,
                            "orderPrice": 25000
                         }
                    ]
                }
            }
            """)))
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getOrderDetail(
            @PathVariable Long orderId,
            @AuthenticationPrincipal(expression = "user") User user) {
        OrderDetailResponse response = orderService.getOrderDetail(orderId, user);
        return ApiResponse.ok(response, "주문 상세 조회 성공");
    }

    @DeleteMapping("/{orderId}")
    @Operation(summary = "주문 취소", description = "주문을 취소합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "취소 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "주문 취소 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "주문 취소 성공",
                "payload": null
            }
            """)))
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal(expression = "user") User user) {
        orderService.cancelOrder(orderId, user);
        return ApiResponse.noContent("주문 취소 성공");
    }
}
