package wsd.bookstore.book.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookUpdateRequest {

    @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다")
    private String title;

    @Size(max = 5000, message = "설명은 5000자를 초과할 수 없습니다")
    private String description;

    @Positive(message = "가격은 양수여야 합니다")
    private Long price;

    @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다")
    private Integer stockQuantity;

    private LocalDateTime publishedAt;

    @Positive(message = "출판사 ID는 양수여야 합니다")
    private Long publisherId;

    private List<@Positive(message = "저자 ID는 양수여야 합니다") Long> authorIds;

    private List<@Positive(message = "카테고리 ID는 양수여야 합니다") Long> categoryIds;
}
