package wsd.bookstore.book.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import wsd.bookstore.book.request.BookCreateRequest;
import wsd.bookstore.book.request.BookSearchCondition;
import wsd.bookstore.book.request.BookUpdateRequest;
import wsd.bookstore.book.response.BookDetailResponse;
import wsd.bookstore.book.response.BookSummaryResponse;
import wsd.bookstore.book.service.BookService;
import wsd.bookstore.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "도서 관리 API")
public class BookController {

    private final BookService bookService;

    @GetMapping
    @Operation(summary = "도서 검색", description = "조건에 맞는 도서를 검색합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "도서 전체 조회 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "도서 목록 조회 성공",
                "payload": [
                    {
                        "bookId": 1,
                        "title": "클린 코드",
                        "author": "로버트 마틴",
                        "price": 30000
                    },
                    {
                        "bookId": 2,
                        "title": "Effective Java",
                        "author": "조슈아 블로크",
                        "price": 45000
                    }
                ]
            }
            """)))
    public ResponseEntity<ApiResponse<Page<BookSummaryResponse>>> getBooks(
            @ModelAttribute BookSearchCondition condition,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<BookSummaryResponse> books = bookService.searchBooks(condition, pageable);
        return ApiResponse.ok(books, "도서 목록 조회 성공");
    }

    @GetMapping("/best-sellers")
    @Operation(summary = "베스트셀러 조회", description = "판매량 상위 10개 도서를 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "베스트셀러 조회 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "베스트셀러 조회 성공",
                "payload": [
                    {
                        "bookId": 1,
                        "title": "클린 코드",
                        "author": "로버트 마틴",
                        "price": 30000
                    },
                    {
                        "bookId": 2,
                        "title": "Effective Java",
                        "author": "조슈아 블로크",
                        "price": 45000
                    }
                ]
            }
            """)))
    public ResponseEntity<ApiResponse<List<BookSummaryResponse>>> getBestSellers() {
        List<BookSummaryResponse> books = bookService.getBestSellers();
        return ApiResponse.ok(books, "베스트셀러 조회 성공");
    }

    @GetMapping("/{id}")
    @Operation(summary = "도서 상세 조회", description = "도서 ID로 상세 정보를 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "도서 상세 조회 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "도서 상세 조회 성공",
                "payload": {
                    "bookId": 10,
                    "title": "클린 코드",
                    "author": "로버트 마틴",
                    "publisher": "인사이트",
                    "isbn": "9788966260959",
                    "price": 30000,
                    "summary": "더 나은 프로그래머가 되기 위한 실천적 가이드",
                    "publicationDate": "2025-03-01"
                }
            }
            """)))
    public ResponseEntity<ApiResponse<BookDetailResponse>> getBook(@PathVariable Long id) {
        BookDetailResponse book = bookService.getBookDetail(id);
        return ApiResponse.ok(book, "도서 상세 조회 성공");
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "도서 등록", description = "관리자가 새로운 도서를 등록합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "생성 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "도서 등록 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "도서 등록 성공",
                "payload": {
                    "bookId": 1
                }
            }
            """)))
    public ResponseEntity<ApiResponse<Void>> createBook(@Valid @RequestBody BookCreateRequest request) {
        BookSummaryResponse response = bookService.createBook(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ApiResponse.created(null, location, "도서 등록 성공");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "도서 수정", description = "관리자가 도서 정보를 수정합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "도서 수정 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "도서 수정 성공",
                "payload": {
                    "bookId": 10,
                    "updatedAt": "2025-09-15T22:01:15.161023"
                }
            }
            """)))
    public ResponseEntity<ApiResponse<BookSummaryResponse>> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookUpdateRequest request) {
        BookSummaryResponse response = bookService.updateBook(id, request);
        return ApiResponse.ok(response, "도서 수정 성공");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "도서 삭제", description = "관리자가 도서를 삭제합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "삭제 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "도서 삭제 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "도서 삭제 성공",
                "payload": {}
            }
            """)))
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ApiResponse.noContent("도서 삭제 성공");
    }
}
