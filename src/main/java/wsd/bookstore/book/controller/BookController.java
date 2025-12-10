package wsd.bookstore.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wsd.bookstore.book.request.BookCreateRequest;
import wsd.bookstore.book.request.BookSearchCondition;
import wsd.bookstore.book.request.BookUpdateRequest;
import wsd.bookstore.book.response.BookDetailResponse;
import wsd.bookstore.book.response.BookSummaryResponse;
import wsd.bookstore.book.service.BookService;
import wsd.bookstore.common.response.ApiResponse;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedModel<BookSummaryResponse>>> searchBooks(
            @ModelAttribute BookSearchCondition condition,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<BookSummaryResponse> response = bookService.searchBooks(condition, pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedModel<>(response), "도서 조회 성공"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDetailResponse>> getBookDetail(@PathVariable Long id) {
        BookDetailResponse response = bookService.getBookDetail(id);
        return ResponseEntity.ok(ApiResponse.success(response, "도서 상세 조회 성공"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<BookSummaryResponse>> createBook(@RequestBody @Valid BookCreateRequest request) {
        BookSummaryResponse response = bookService.createBook(request);
        return ResponseEntity.ok(ApiResponse.success(response, "도서 등록 성공"));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<BookSummaryResponse>> updateBook(
            @PathVariable Long id,
            @RequestBody @Valid BookUpdateRequest request) {
        BookSummaryResponse response = bookService.updateBook(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "도서 수정 성공"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(ApiResponse.noContent("도서 삭제 성공"));
    }
}
