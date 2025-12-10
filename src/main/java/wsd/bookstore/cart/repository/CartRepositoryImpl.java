package wsd.bookstore.cart.repository;

import static wsd.bookstore.book.entity.QBook.book;
import static wsd.bookstore.cart.entity.QCart.cart;
import static wsd.bookstore.cart.entity.QCartItem.cartItem;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import wsd.bookstore.cart.entity.CartStatus;
import wsd.bookstore.cart.response.CartItemResponse;
import wsd.bookstore.cart.response.QCartItemResponse;

@Repository
@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CartItemResponse> findCartItems(Long userId) {
        return queryFactory
                .select(new QCartItemResponse(
                        book.id,
                        book.title,
                        book.price,
                        cartItem.quantity,
                        cartItem.updatedAt))
                .from(cartItem)
                .join(cartItem.cart, cart)
                .join(cartItem.book, book)
                .where(
                        cart.user.id.eq(userId),
                        cart.status.eq(CartStatus.ACTIVE))
                .fetch();
    }
}
