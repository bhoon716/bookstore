package wsd.bookstore.review.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import wsd.bookstore.review.entity.ReviewLike;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

    boolean existsByUserIdAndReviewId(Long userId, Long reviewId);

    Optional<ReviewLike> findByUserIdAndReviewId(Long userId, Long reviewId);
}
