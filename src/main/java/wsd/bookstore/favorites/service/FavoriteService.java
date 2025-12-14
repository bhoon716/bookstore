package wsd.bookstore.favorites.service;

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
import wsd.bookstore.favorites.entity.Favorite;
import wsd.bookstore.favorites.repository.FavoriteRepository;
import wsd.bookstore.user.entity.User;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final BookRepository bookRepository;

    public List<BookSummaryResponse> getMyFavorites(User user) {
        log.info("좋아요 목록 조회 요청: userId={}", user.getId());
        return favoriteRepository.findAllByUser(user).stream()
                .map(favorite -> BookSummaryResponse.from(favorite.getBook()))
                .toList();
    }

    @Transactional
    public void addFavorite(User user, Long bookId) {
        log.info("좋아요 추가 요청: bookId={}, userId={}", bookId, user.getId());
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOOK));

        if (favoriteRepository.existsByUserAndBook(user, book)) {
            throw new CustomException(ErrorCode.DUPLICATE_FAVORITE);
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .book(book)
                .build();

        favoriteRepository.save(favorite);
        log.info("좋아요 추가 완료: bookId={}, userId={}", bookId, user.getId());
    }

    @Transactional
    public void deleteFavorite(User user, Long bookId) {
        log.info("좋아요 취소 요청: bookId={}, userId={}", bookId, user.getId());
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOOK));

        Favorite favorite = favoriteRepository.findByUserAndBook(user, book)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_FAVORITE));

        favoriteRepository.delete(favorite);
        log.info("좋아요 취소 완료: bookId={}, userId={}", bookId, user.getId());
    }
}
