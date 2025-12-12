package wsd.bookstore.review.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Schema(description = "리뷰 정보 응답 DTO")
public class ReviewResponse {

    @Schema(description = "리뷰 ID", example = "101")
    private final Long reviewId;

    @Schema(description = "도서 ID", example = "10")
    private final Long bookId;

    @Schema(description = "작성자 ID", example = "50")
    private final Long reviewerId;

    @Schema(description = "작성자 이름", example = "홍길동")
    private final String reviewerName;

    @Schema(description = "평점", example = "5")
    private final Integer rating;

    @Schema(description = "리뷰 내용", example = "정말 유익한 책입니다!")
    private final String content;

    @Schema(description = "좋아요 수", example = "12")
    private final Integer likeCount;

    @Schema(description = "작성 일시", example = "2025-03-10T11:20:00")
    private final LocalDateTime createdAt;

    @QueryProjection
    public ReviewResponse(Long reviewId, Long bookId, Long reviewerId, String reviewerName,
            Integer rating, String content, Integer likeCount, LocalDateTime createdAt) {
        this.reviewId = reviewId;
        this.bookId = bookId;
        this.reviewerId = reviewerId;
        this.reviewerName = reviewerName;
        this.rating = rating;
        this.content = content;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
    }
}
