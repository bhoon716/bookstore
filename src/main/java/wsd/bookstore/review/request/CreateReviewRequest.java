package wsd.bookstore.review.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "리뷰 작성 요청 DTO")
public class CreateReviewRequest {

    @NotNull(message = "평점은 필수입니다.")
    @Min(value = 1, message = "평점은 1점 이상이어야 합니다.")
    @Max(value = 5, message = "평점은 5점 이하이어야 합니다.")
    @Schema(description = "평점 (1~5)", example = "5", minimum = "1", maximum = "5")
    private Integer rating;

    @NotBlank(message = "내용은 필수입니다.")
    @Schema(description = "리뷰 내용", example = "정말 유익한 책입니다!")
    private String content;

    public CreateReviewRequest(Integer rating, String content) {
        this.rating = rating;
        this.content = content;
    }
}
