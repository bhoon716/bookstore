package wsd.bookstore.cart.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@NoArgsConstructor
@Schema(description = "장바구니 항목 수정 요청 DTO")
public class UpdateCartItemRequest {

    @NotNull(message = "수량은 필수입니다.")
    @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
    @Max(value = 99, message = "수량은 99 이하여야 합니다.")
    @Schema(description = "변경할 수량 (1~99)", example = "5", minimum = "1", maximum = "99")
    private Integer quantity;
}
