package wsd.bookstore.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wsd.bookstore.review.entity.ReviewLike;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
}
