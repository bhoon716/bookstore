package wsd.bookstore.wishlist.controller;

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
import wsd.bookstore.security.auth.CustomUserDetails;
import wsd.bookstore.wishlist.service.WishlistService;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<List<BookSummaryResponse>> getMyWishlist(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(wishlistService.getMyWishlist(userDetails.getUser()));
    }

    @PostMapping("/{bookId}")
    public void addWishlist(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long bookId) {
        wishlistService.addWishlist(userDetails.getUser(), bookId);
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteWishlist(@AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long bookId) {
        wishlistService.deleteWishlist(userDetails.getUser(), bookId);
        return ResponseEntity.noContent().build();
    }
}
