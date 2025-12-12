package wsd.bookstore.cart.response;

import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import wsd.bookstore.cart.entity.Cart;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Builder
@Schema(description = "장바구니 전체 정보를 담는 응답 DTO")
public class CartResponse {

        @Schema(description = "장바구니 ID", example = "100")
        private Long id;

        @Schema(description = "장바구니에 담긴 총 도서 수량", example = "3")
        private int totalCount;

        @Schema(description = "장바구니 총 금액 (원)", example = "75000")
        private long totalPrice;

        @Schema(description = "장바구니 항목 리스트")
        private List<CartItemResponse> items;

        public static CartResponse from(Cart cart, List<CartItemResponse> cartItems) {
                if (cart == null) {
                        return CartResponse.builder()
                                        .id(null)
                                        .totalCount(0)
                                        .totalPrice(0)
                                        .items(Collections.emptyList())
                                        .build();
                }

                int totalCount = cartItems.stream()
                                .mapToInt(CartItemResponse::getQuantity)
                                .sum();

                long totalPrice = cartItems.stream()
                                .mapToLong(item -> item.getUnitPrice() * item.getQuantity())
                                .sum();

                return CartResponse.builder()
                                .id(cart.getId())
                                .totalCount(totalCount)
                                .totalPrice(totalPrice)
                                .items(cartItems)
                                .build();
        }
}
