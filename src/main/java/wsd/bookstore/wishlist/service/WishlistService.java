package wsd.bookstore.wishlist.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final BookRepository bookRepository;

    public List<BookSummaryResponse> getMyWishlist(User user) {
        log.info("위시리스트 목록 조회 요청: userId={}", user.getId());
        return wishlistRepository.findAllByUser(user).stream()
                .map(wishlist -> BookSummaryResponse.from(wishlist.getBook()))
                .toList();
    }

    @Transactional
    public void addWishlist(User user, Long bookId) {
        log.info("위시리스트 추가 요청: bookId={}, userId={}", bookId, user.getId());
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
        log.info("위시리스트 추가 완료: bookId={}, userId={}", bookId, user.getId());
    }

    @Transactional
    public void deleteWishlist(User user, Long bookId) {
        log.info("위시리스트 삭제 요청: bookId={}, userId={}", bookId, user.getId());
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOOK));

        Wishlist wishlist = wishlistRepository.findByUserAndBook(user, book)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_WISHLIST));

        wishlistRepository.delete(wishlist);
        log.info("위시리스트 삭제 완료: bookId={}, userId={}", bookId, user.getId());
    }
}
