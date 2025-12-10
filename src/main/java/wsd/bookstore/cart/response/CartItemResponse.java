package wsd.bookstore.cart.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;
import wsd.bookstore.book.entity.Book;
import wsd.bookstore.book.response.BookSummaryResponse;
import wsd.bookstore.cart.entity.CartItem;

@Getter
public class CartItemResponse {

    private Long id;
    private BookSummaryResponse book;
    private Integer quantity;
    private LocalDateTime updatedAt;

    @QueryProjection
    public CartItemResponse(Long id, Book book, Integer quantity, LocalDateTime updatedAt) {
        this.id = id;
        this.book = BookSummaryResponse.from(book);
        this.quantity = quantity;
        this.updatedAt = updatedAt;
    }

    public static CartItemResponse from(CartItem cartItem) {
        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getBook(),
                cartItem.getQuantity(),
                cartItem.getUpdatedAt());
    }
}
