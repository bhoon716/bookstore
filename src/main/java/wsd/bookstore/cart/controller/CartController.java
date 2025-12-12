package wsd.bookstore.cart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wsd.bookstore.cart.request.AddCartItemRequest;
import wsd.bookstore.cart.request.UpdateCartItemRequest;
import wsd.bookstore.cart.response.CartResponse;
import wsd.bookstore.cart.service.CartService;
import wsd.bookstore.common.response.ApiResponse;
import wsd.bookstore.security.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
@Tag(name = "Carts", description = "장바구니 API")
public class CartController {

    private final CartService cartService;

    @GetMapping("/my")
    @Operation(summary = "장바구니 조회", description = "내 장바구니 항목을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "장바구니 조회 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "장바구니 조회 성공",
                "payload": {
                    "id": 100,
                    "totalCount": 2,
                    "totalPrice": 75000,
                    "items": [
                        {
                            "cartItemId": 1,
                            "bookId": 10,
                            "title": "클린 코드",
                            "quantity": 1,
                            "price": 30000,
                            "totalPrice": 30000
                        },
                         {
                            "cartItemId": 2,
                            "bookId": 2,
                            "title": "Effective Java",
                            "quantity": 1,
                            "price": 45000,
                            "totalPrice": 45000
                        }
                    ]
                }
            }
            """)))
    public ResponseEntity<ApiResponse<CartResponse>> getMyCart(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CartResponse cart = cartService.getMyCart(userDetails.getUser());
        return ApiResponse.ok(cart, "장바구니 조회 성공");
    }

    @PostMapping("/items")
    @Operation(summary = "장바구니 항목 추가", description = "장바구니에 상품을 추가합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "추가 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "장바구니 추가 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "장바구니에 추가되었습니다.",
                "payload": null
            }
            """)))
    public ResponseEntity<ApiResponse<Void>> addItemToCart(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid AddCartItemRequest request) {
        cartService.addCartItem(request, userDetails.getUser());
        return ApiResponse.ok(null, "장바구니에 추가되었습니다.");
    }

    @PatchMapping("/items/{cartItemId}")
    @Operation(summary = "장바구니 항목 수량 변경", description = "장바구니 항목의 수량을 변경합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "장바구니 수정 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "장바구니 업데이트 성공",
                "payload": {
                     "id": 100,
                    "totalCount": 3,
                    "totalPrice": 105000,
                    "items": [
                        {
                            "cartItemId": 1,
                            "bookId": 10,
                            "title": "클린 코드",
                            "quantity": 2,
                            "price": 30000,
                            "totalPrice": 60000
                        },
                         {
                            "cartItemId": 2,
                            "bookId": 2,
                            "title": "Effective Java",
                            "quantity": 1,
                            "price": 45000,
                            "totalPrice": 45000
                        }
                    ]
                }
            }
            """)))
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(
            @PathVariable Long cartItemId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid UpdateCartItemRequest request) {
        CartResponse cart = cartService.updateCartItem(cartItemId, request, userDetails.getUser());
        return ApiResponse.ok(cart, "장바구니 업데이트 성공");
    }

    @DeleteMapping("/items/{cartItemId}")
    @Operation(summary = "장바구니 항목 삭제", description = "장바구니 항목을 삭제합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "삭제 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "장바구니 삭제 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "장바구니 삭제 성공",
                "payload": null
            }
            """)))
    public ResponseEntity<ApiResponse<Void>> removeCartItem(
            @PathVariable Long cartItemId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.removeCartItem(cartItemId, userDetails.getUser());
        return ApiResponse.noContent("장바구니 삭제 성공");
    }

    @DeleteMapping("/my")
    @Operation(summary = "장바구니 비우기", description = "장바구니의 모든 항목을 삭제합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "비우기 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "장바구니 비우기 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "장바구니에서 삭제되었습니다.",
                "payload": null
            }
            """)))
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.clearCart(userDetails.getUser());
        return ApiResponse.noContent("장바구니에서 삭제되었습니다.");
    }
}
