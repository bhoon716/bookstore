package wsd.bookstore.book.repository;

import static wsd.bookstore.book.entity.QAuthor.author;
import static wsd.bookstore.book.entity.QBook.book;
import static wsd.bookstore.book.entity.QBookAuthor.bookAuthor;
import static wsd.bookstore.book.entity.QBookCategory.bookCategory;
import static wsd.bookstore.book.entity.QCategory.category;
import static wsd.bookstore.book.entity.QPublisher.publisher;
import static wsd.bookstore.order.entity.QOrderItem.orderItem;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import wsd.bookstore.book.entity.Book;
import wsd.bookstore.book.request.BookSearchCondition;
import wsd.bookstore.book.response.BookSummaryResponse;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<BookSummaryResponse> search(BookSearchCondition condition, Pageable pageable) {
        // 조건에 맞는 Book Id 가져오기
        List<Long> bookIds = fetchBookIds(condition, pageable);

        // 검색 결과 없는 경우
        if (bookIds.isEmpty()) {
            JPAQuery<Long> countQuery = createCountQuery(condition);
            return PageableExecutionUtils.getPage(new ArrayList<>(), pageable, countQuery::fetchOne);
        }

        // Book Ids로 책 정보 전부 가져오기
        List<Book> books = fetchBooksWithDetails(bookIds);

        Map<Long, Book> bookMap = books.stream()
                .collect(Collectors.toMap(Book::getId, b -> b));

        List<BookSummaryResponse> content = bookIds.stream()
                .map(bookMap::get)
                .map(BookSummaryResponse::from)
                .toList();

        JPAQuery<Long> countQuery = createCountQuery(condition);
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public List<BookSummaryResponse> findBestSellers(int limit) {
        List<Book> books = queryFactory
                .select(book)
                .from(orderItem)
                .join(orderItem.book, book)
                .join(book.publisher, publisher).fetchJoin()
                .groupBy(book.id)
                .orderBy(orderItem.quantity.sum().desc())
                .limit(limit)
                .fetch();

        return books.stream()
                .map(BookSummaryResponse::from)
                .toList();
    }

    private List<Long> fetchBookIds(BookSearchCondition condition, Pageable pageable) {
        BooleanExpression[] predicates = createPredicates(condition);

        return queryFactory
                .select(book.id)
                .from(book)
                .leftJoin(book.publisher, publisher)
                .leftJoin(book.bookCategories, bookCategory)
                .leftJoin(bookCategory.category, category)
                .leftJoin(book.bookAuthors, bookAuthor)
                .leftJoin(bookAuthor.author, author)
                .where(predicates)
                .groupBy(book.id)
                .orderBy(getOrderSpecifier(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private List<Book> fetchBooksWithDetails(List<Long> bookIds) {
        return queryFactory
                .selectFrom(book)
                .distinct()
                .leftJoin(book.publisher, publisher).fetchJoin()
                .leftJoin(book.bookAuthors, bookAuthor).fetchJoin()
                .leftJoin(bookAuthor.author, author).fetchJoin()
                .where(book.id.in(bookIds))
                .fetch();
    }

    private JPAQuery<Long> createCountQuery(BookSearchCondition condition) {
        BooleanExpression[] predicates = createPredicates(condition);

        JPAQuery<Long> query = queryFactory
                .select(book.countDistinct())
                .from(book);

        if (needsPublisherJoin(condition)) {
            query.leftJoin(book.publisher, publisher);
        }
        if (needsCategoryJoin(condition)) {
            query.leftJoin(book.bookCategories, bookCategory)
                    .leftJoin(bookCategory.category, category);
        }
        if (needsAuthorJoin(condition)) {
            query.leftJoin(book.bookAuthors, bookAuthor)
                    .leftJoin(bookAuthor.author, author);
        }

        return query.where(predicates);
    }

    private BooleanExpression[] createPredicates(BookSearchCondition condition) {
        return new BooleanExpression[] {
                titleContains(condition.getKeyword()),
                categoryIdEq(condition.getCategoryId()),
                authorIdEq(condition.getAuthorId()),
                publisherIdEq(condition.getPublisherId())
        };
    }

    private boolean needsPublisherJoin(BookSearchCondition condition) {
        return StringUtils.hasText(condition.getKeyword()) || condition.getPublisherId() != null;
    }

    private boolean needsCategoryJoin(BookSearchCondition condition) {
        return condition.getCategoryId() != null;
    }

    private boolean needsAuthorJoin(BookSearchCondition condition) {
        return StringUtils.hasText(condition.getKeyword()) || condition.getAuthorId() != null;
    }

    private BooleanExpression titleContains(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        return book.title.containsIgnoreCase(keyword);
    }

    private BooleanExpression categoryIdEq(Long categoryId) {
        return categoryId != null ? category.id.eq(categoryId) : null;
    }

    private BooleanExpression authorIdEq(Long authorId) {
        return authorId != null ? author.id.eq(authorId) : null;
    }

    private BooleanExpression publisherIdEq(Long publisherId) {
        return publisherId != null ? publisher.id.eq(publisherId) : null;
    }

    private OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (!pageable.getSort().isEmpty()) {
            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                switch (order.getProperty()) {
                    case "title" -> orders.add(new OrderSpecifier<>(direction, book.title));
                    case "price" -> orders.add(new OrderSpecifier<>(direction, book.price));
                    case "publishedAt" -> orders.add(new OrderSpecifier<>(direction, book.publishedAt));
                    default -> orders.add(new OrderSpecifier<>(direction, book.createdAt));
                }
            }
        } else {
            orders.add(new OrderSpecifier<>(Order.DESC, book.createdAt));
        }

        return orders.toArray(OrderSpecifier[]::new);
    }
}
