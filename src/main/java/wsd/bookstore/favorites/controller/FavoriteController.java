package wsd.bookstore.favorites.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wsd.bookstore.book.response.BookSummaryResponse;
import wsd.bookstore.favorites.service.FavoriteService;
import wsd.bookstore.security.auth.CustomUserDetails;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<List<BookSummaryResponse>> getMyFavorites(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(favoriteService.getMyFavorites(userDetails.getUser()));
    }

    @PostMapping("/{bookId}")
    public void addFavorite(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long bookId) {
        favoriteService.addFavorite(userDetails.getUser(), bookId);
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteFavorite(@AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long bookId) {
        favoriteService.deleteFavorite(userDetails.getUser(), bookId);
        return ResponseEntity.noContent().build();
    }
}
