package wsd.bookstore.book.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookCreateRequest {

    @NotBlank(message = "ISBN은 필수입니다")
    @Pattern(regexp = "^\\d{13}$", message = "ISBN은 정확히 13자리 숫자여야 합니다")
    private String isbn13;

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다")
    private String title;

    @Size(max = 5000, message = "설명은 5000자를 초과할 수 없습니다")
    private String description;

    @NotNull(message = "가격은 필수입니다")
    @Positive(message = "가격은 양수여야 합니다")
    private Long price;

    @NotNull(message = "재고 수량은 필수입니다")
    @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다")
    private Integer stockQuantity;

    private LocalDateTime publishedAt;

    @NotNull(message = "출판사 ID는 필수입니다")
    @Positive(message = "출판사 ID는 양수여야 합니다")
    private Long publisherId;

    @NotEmpty(message = "최소 한 명의 저자가 필요합니다")
    private List<@Positive(message = "저자 ID는 양수여야 합니다") Long> authorIds;

    @NotEmpty(message = "최소 하나의 카테고리가 필요합니다")
    private List<@Positive(message = "카테고리 ID는 양수여야 합니다") Long> categoryIds;
}
