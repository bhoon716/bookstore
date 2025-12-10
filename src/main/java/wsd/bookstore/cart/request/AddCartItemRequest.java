package wsd.bookstore.cart.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddCartItemRequest {

    @NotNull(message = "책 ID는 필수입니다.")
    private Long bookId;

    @NotNull(message = "수량은 필수입니다.")
    @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
    @Max(value = 99, message = "수량은 99 이하여야 합니다.")
    private Integer quantity;
}
