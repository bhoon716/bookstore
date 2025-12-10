package wsd.bookstore.wishlist.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wsd.bookstore.book.entity.Book;
import wsd.bookstore.book.repository.BookRepository;
import wsd.bookstore.book.response.BookSummaryResponse;
import wsd.bookstore.common.error.CustomException;
import wsd.bookstore.common.error.ErrorCode;
import wsd.bookstore.user.entity.User;
import wsd.bookstore.wishlist.entity.Wishlist;
import wsd.bookstore.wishlist.repository.WishlistRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final BookRepository bookRepository;

    public List<BookSummaryResponse> getMyWishlist(User user) {
        return wishlistRepository.findAllByUser(user).stream()
                .map(wishlist -> BookSummaryResponse.from(wishlist.getBook()))
                .toList();
    }

    @Transactional
    public void addWishlist(User user, Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOOK));

        if (wishlistRepository.existsByUserAndBook(user, book)) {
            throw new CustomException(ErrorCode.DUPLICATE_WISHLIST);
        }

        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .book(book)
                .build();

        wishlistRepository.save(wishlist);
    }

    @Transactional
    public void deleteWishlist(User user, Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOOK));

        Wishlist wishlist = wishlistRepository.findByUserAndBook(user, book)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_WISHLIST));

        wishlistRepository.delete(wishlist);
    }
}
