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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<CartResponse>> getMyCart(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CartResponse cart = cartService.getMyCart(userDetails.getUser());
        return ApiResponse.ok(cart, "장바구니 조회 성공");
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<Void>> addItemToCart(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid AddCartItemRequest request) {
        cartService.addCartItem(request, userDetails.getUser());
        return ApiResponse.ok(null, "장바구니에 추가되었습니다.");
    }

    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(
            @PathVariable Long cartItemId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid UpdateCartItemRequest request) {
        CartResponse cart = cartService.updateCartItem(cartItemId, request, userDetails.getUser());
        return ApiResponse.ok(cart, "장바구니 업데이트 성공");
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<Void>> removeCartItem(
            @PathVariable Long cartItemId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.removeCartItem(cartItemId, userDetails.getUser());
        return ApiResponse.noContent("장바구니 삭제 성공");
    }

    @DeleteMapping("/my")
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.clearCart(userDetails.getUser());
        return ApiResponse.noContent("장바구니에서 삭제되었습니다.");
    }
}
