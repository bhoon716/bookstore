package wsd.bookstore.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wsd.bookstore.book.request.AuthorRequest;
import wsd.bookstore.book.service.AuthorService;
import wsd.bookstore.common.response.ApiResponse;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> createAuthor(@Valid @RequestBody AuthorRequest request) {
        authorService.createAuthor(request);
        return ResponseEntity.ok(ApiResponse.noContent("작가 등록 성공"));
    }

    @PutMapping("/{authorId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateAuthor(
            @PathVariable Long authorId,
            @Valid @RequestBody AuthorRequest request) {
        authorService.updateAuthor(authorId, request);
        return ResponseEntity.ok(ApiResponse.noContent("작가 수정 성공"));
    }

    @DeleteMapping("/{authorId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAuthor(@PathVariable Long authorId) {
        authorService.deleteAuthor(authorId);
        return ResponseEntity.ok(ApiResponse.noContent("작가 삭제 성공"));
    }
}
