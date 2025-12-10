package wsd.bookstore.favorites.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wsd.bookstore.book.response.BookSummaryResponse;
import wsd.bookstore.favorites.repository.FavoriteRepository;
import wsd.bookstore.user.entity.User;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    public List<BookSummaryResponse> getMyFavorites(User user) {
        return favoriteRepository.findAllByUser(user).stream()
                .map(favorite -> BookSummaryResponse.from(favorite.getBook()))
                .toList();
    }
}
