package wsd.bookstore.review.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class MyReviewResponse {

    private final Long reviewId;
    private final Long bookId;
    private final String bookTitle;
    private final Integer rating;
    private final String title;
    private final String content;
    private final LocalDateTime reviewedAt;

    @QueryProjection
    public MyReviewResponse(Long reviewId, Long bookId, String bookTitle, Integer rating,
            String title, String content, LocalDateTime reviewedAt) {
        this.reviewId = reviewId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.rating = rating;
        this.title = title;
        this.content = content;
        this.reviewedAt = reviewedAt;
    }
}
