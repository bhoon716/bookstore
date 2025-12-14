package wsd.bookstore.review.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Schema(description = "내가 작성한 리뷰 정보 응답 DTO")
public class MyReviewResponse {

    @Schema(description = "리뷰 ID", example = "101")
    private final Long reviewId;

    @Schema(description = "도서 ID", example = "10")
    private final Long bookId;

    @Schema(description = "도서 제목", example = "Clean Code")
    private final String bookTitle;

    @Schema(description = "평점", example = "5")
    private final Integer rating;

    @Schema(description = "리뷰 내용", example = "정말 유익한 책입니다!")
    private final String content;

    @Schema(description = "작성 일시", example = "2025-03-10T11:20:00")
    private final LocalDateTime reviewedAt;

    @QueryProjection
    public MyReviewResponse(Long reviewId, Long bookId, String bookTitle, Integer rating,
            String content, LocalDateTime reviewedAt) {
        this.reviewId = reviewId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.rating = rating;
        this.content = content;
        this.reviewedAt = reviewedAt;
    }
}
