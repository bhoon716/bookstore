package wsd.bookstore.favorites.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final BookRepository bookRepository;

    public List<BookSummaryResponse> getMyFavorites(User user) {
        return favoriteRepository.findAllByUser(user).stream()
                .map(favorite -> BookSummaryResponse.from(favorite.getBook()))
                .toList();
    }

    @Transactional
    public void addFavorite(User user, Long bookId) {
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
    }

    @Transactional
    public void deleteFavorite(User user, Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOOK));

        Favorite favorite = favoriteRepository.findByUserAndBook(user, book)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_FAVORITE));

        favoriteRepository.delete(favorite);
    }
}
