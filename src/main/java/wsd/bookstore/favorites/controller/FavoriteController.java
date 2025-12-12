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
import wsd.bookstore.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites")
@Tag(name = "Favorites", description = "즐겨찾기 API")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    @Operation(summary = "즐겨찾기 목록 조회", description = "내 즐겨찾기 도서 목록을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "즐겨찾기 목록 조회 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "즐겨찾기 목록 조회 성공",
                "payload": [
                    {
                        "bookId": 12,
                        "title": "Clean Code",
                        "author": "Robert C. Martin",
                        "price": 30000
                    }
                ]
            }
            """)))
    public ResponseEntity<ApiResponse<List<BookSummaryResponse>>> getMyFavorites(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.ok(favoriteService.getMyFavorites(userDetails.getUser()), "즐겨찾기 목록 조회 성공");
    }

    @PostMapping("/{bookId}")
    @Operation(summary = "즐겨찾기 추가", description = "도서를 즐겨찾기에 추가합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "추가 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "즐겨찾기 추가 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "즐겨찾기 추가 성공",
                "payload": null
            }
            """)))
    public ResponseEntity<ApiResponse<Void>> addFavorite(@AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long bookId) {
        favoriteService.addFavorite(userDetails.getUser(), bookId);
        return ApiResponse.ok(null, "즐겨찾기 추가 성공");
    }

    @DeleteMapping("/{bookId}")
    @Operation(summary = "즐겨찾기 삭제", description = "즐겨찾기에서 도서를 삭제합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "삭제 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "즐겨찾기 삭제 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "즐겨찾기 삭제 성공",
                "payload": null
            }
            """)))
    public ResponseEntity<ApiResponse<Void>> deleteFavorite(@AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long bookId) {
        favoriteService.deleteFavorite(userDetails.getUser(), bookId);
        return ApiResponse.noContent("즐겨찾기 삭제 성공");
    }
}
