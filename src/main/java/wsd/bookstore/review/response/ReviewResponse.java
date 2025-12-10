package wsd.bookstore.review.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ReviewResponse {

    private final Long reviewId;
    private final Long bookId;
    private final Long reviewerId;
    private final String reviewerName;
    private final Integer rating;
    private final String title;
    private final String body;
    private final Integer likeCount;
    private final LocalDateTime createdAt;

    @QueryProjection
    public ReviewResponse(Long reviewId, Long bookId, Long reviewerId, String reviewerName,
            Integer rating, String title, String body, Integer likeCount,
            LocalDateTime createdAt) {
        this.reviewId = reviewId;
        this.bookId = bookId;
        this.reviewerId = reviewerId;
        this.reviewerName = reviewerName;
        this.rating = rating;
        this.title = title;
        this.body = body;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
    }
}
