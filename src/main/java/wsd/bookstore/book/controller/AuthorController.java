package wsd.bookstore.book.controller;

import java.net.URI;
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
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import wsd.bookstore.book.response.AuthorResponse;

import wsd.bookstore.book.request.AuthorRequest;
import wsd.bookstore.book.service.AuthorService;
import wsd.bookstore.common.response.ApiResponse;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AuthorResponse>>> getAuthors() {
        List<AuthorResponse> authors = authorService.getAuthors();
        return ApiResponse.ok(authors, "작가 목록 조회 성공");
    }

    @GetMapping("/{authorId}")
    public ResponseEntity<ApiResponse<AuthorResponse>> getAuthor(@PathVariable Long authorId) {
        AuthorResponse response = authorService.getAuthor(authorId);
        return ApiResponse.ok(response, "작가 상세 조회 성공");
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> createAuthor(@Valid @RequestBody AuthorRequest request) {
        Long authorId = authorService.createAuthor(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(authorId)
                .toUri();
        return ApiResponse.created(null, location, "작가 등록 성공");
    }

    @PutMapping("/{authorId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateAuthor(
            @PathVariable Long authorId,
            @Valid @RequestBody AuthorRequest request) {
        authorService.updateAuthor(authorId, request);
        return ApiResponse.ok(null, "작가 수정 성공");
    }

    @DeleteMapping("/{authorId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAuthor(@PathVariable Long authorId) {
        authorService.deleteAuthor(authorId);
        return ApiResponse.noContent("작가 삭제 성공");
    }
}
