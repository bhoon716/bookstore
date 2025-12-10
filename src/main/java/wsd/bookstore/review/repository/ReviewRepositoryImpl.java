package wsd.bookstore.review.repository;

import static wsd.bookstore.book.entity.QBook.book;
import static wsd.bookstore.review.entity.QReview.review;
import static wsd.bookstore.user.entity.QUser.user;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import wsd.bookstore.review.response.MyReviewResponse;
import wsd.bookstore.review.response.QMyReviewResponse;
import wsd.bookstore.review.response.QReviewResponse;
import wsd.bookstore.review.response.ReviewResponse;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ReviewResponse> getReviews(Long bookId, Pageable pageable) {
        List<ReviewResponse> content = fetchReviews(bookId, pageable);
        JPAQuery<Long> countQuery = createCountQuery(bookId);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<MyReviewResponse> findMyReviews(Long userId, Pageable pageable) {
        List<MyReviewResponse> content = queryFactory
                .select(new QMyReviewResponse(
                        review.id,
                        review.book.id,
                        review.book.title,
                        review.rating,
                        review.body,
                        review.createdAt))
                .from(review)
                .join(review.book, book)
                .where(userIdEq(userId))
                .orderBy(review.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(review.count())
                .from(review)
                .where(userIdEq(userId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private List<ReviewResponse> fetchReviews(Long bookId, Pageable pageable) {
        return queryFactory
                .select(new QReviewResponse(
                        review.id,
                        review.book.id,
                        review.user.id,
                        user.username,
                        review.rating,
                        review.body,
                        review.likeCount,
                        review.createdAt))
                .from(review)
                .join(review.user, user)
                .where(bookIdEq(bookId))
                .orderBy(getOrderSpecifier(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private JPAQuery<Long> createCountQuery(Long bookId) {
        return queryFactory
                .select(review.count())
                .from(review)
                .where(bookIdEq(bookId));
    }

    private BooleanExpression bookIdEq(Long bookId) {
        return bookId != null ? review.book.id.eq(bookId) : null;
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? review.user.id.eq(userId) : null;
    }

    private OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (pageable.getSort().isEmpty()) {
            return new OrderSpecifier[] { new OrderSpecifier<>(Order.DESC, review.createdAt) };
        }

        for (Sort.Order order : pageable.getSort()) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "likeCount" -> {
                    orders.add(new OrderSpecifier<>(direction, review.likeCount));
                    orders.add(new OrderSpecifier<>(Order.DESC, review.createdAt));
                }
                case "createdAt" -> orders.add(new OrderSpecifier<>(direction, review.createdAt));
                default -> orders.add(new OrderSpecifier<>(Order.DESC, review.createdAt));
            }
        }

        return orders.toArray(OrderSpecifier[]::new);
    }
}
