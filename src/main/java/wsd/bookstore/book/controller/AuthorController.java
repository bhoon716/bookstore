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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
@Tag(name = "Authors", description = "작가 관리 API")
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    @Operation(summary = "작가 목록 조회", description = "전체 작가 목록을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "작가 목록 조회 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "작가 목록 조회 성공",
                "payload": [
                    {
                        "authorId": 1,
                        "name": "로버트 마틴"
                    },
                    {
                        "authorId": 2,
                        "name": "조슈아 블로크"
                    }
                ]
            }
            """)))
    public ResponseEntity<ApiResponse<List<AuthorResponse>>> getAuthors() {
        List<AuthorResponse> authors = authorService.getAuthors();
        return ApiResponse.ok(authors, "작가 목록 조회 성공");
    }

    @GetMapping("/{authorId}")
    @Operation(summary = "작가 상세 조회", description = "작가 ID로 상세 정보를 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "작가 상세 조회 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "작가 상세 조회 성공",
                "payload": {
                    "authorId": 1,
                    "name": "로버트 마틴"
                }
            }
            """)))
    public ResponseEntity<ApiResponse<AuthorResponse>> getAuthor(@PathVariable Long authorId) {
        AuthorResponse response = authorService.getAuthor(authorId);
        return ApiResponse.ok(response, "작가 상세 조회 성공");
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "작가 등록", description = "관리자가 새로운 작가를 등록합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "생성 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "작가 등록 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "작가 등록 성공",
                "payload": null
            }
            """)))
    public ResponseEntity<ApiResponse<Void>> createAuthor(@Valid @RequestBody AuthorRequest request) {
        Long authorId = authorService.createAuthor(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(authorId)
                .toUri();
        return ApiResponse.created(null, location, "작가 등록 성공");
    }

    @PutMapping("/{authorId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "작가 수정", description = "관리자가 작가 정보를 수정합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "작가 수정 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "작가 수정 성공",
                "payload": null
            }
            """)))
    public ResponseEntity<ApiResponse<Void>> updateAuthor(
            @PathVariable Long authorId,
            @Valid @RequestBody AuthorRequest request) {
        authorService.updateAuthor(authorId, request);
        return ApiResponse.ok(null, "작가 수정 성공");
    }

    @DeleteMapping("/{authorId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "작가 삭제", description = "관리자가 작가를 삭제합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "삭제 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "작가 삭제 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "작가 삭제 성공",
                "payload": null
            }
            """)))
    public ResponseEntity<ApiResponse<Void>> deleteAuthor(@PathVariable Long authorId) {
        authorService.deleteAuthor(authorId);
        return ApiResponse.noContent("작가 삭제 성공");
    }
}
