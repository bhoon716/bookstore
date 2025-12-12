package wsd.bookstore.book.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@AllArgsConstructor
@Schema(description = "도서 수정 요청 DTO")
public class BookUpdateRequest {

    @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다")
    @Schema(description = "도서 제목 (수정 시)", example = "Effective Java 3/E", maxLength = 255)
    private String title;

    @Size(max = 5000, message = "설명은 5000자를 초과할 수 없습니다")
    @Schema(description = "도서 설명 (수정 시)", example = "자바 프로그래머를 위한 필독서", maxLength = 5000)
    private String description;

    @Positive(message = "가격은 양수여야 합니다")
    @Schema(description = "가격 (수정 시)", example = "40000", minimum = "0")
    private Long price;

    @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다")
    @Schema(description = "재고 수량 (수정 시)", example = "200", minimum = "0")
    private Integer stockQuantity;

    @Schema(description = "출판일 (수정 시)", example = "2019-01-01T00:00:00")
    private LocalDateTime publishedAt;

    @Positive(message = "출판사 ID는 양수여야 합니다")
    @Schema(description = "출판사 ID (수정 시)", example = "1")
    private Long publisherId;

    @Schema(description = "저자 ID 목록 (수정 시)", example = "[1]")
    private List<@Positive(message = "저자 ID는 양수여야 합니다") Long> authorIds;

    @Schema(description = "카테고리 ID 목록 (수정 시)", example = "[10]")
    private List<@Positive(message = "카테고리 ID는 양수여야 합니다") Long> categoryIds;
}
