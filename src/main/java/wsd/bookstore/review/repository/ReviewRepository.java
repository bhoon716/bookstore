package wsd.bookstore.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wsd.bookstore.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

    boolean existsByUserIdAndBookId(Long userId, Long bookId);
}
