package wsd.bookstore.book.controller;

import jakarta.validation.Valid;
import java.net.URI;
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

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BookSummaryResponse>>> getBooks(
            @ModelAttribute BookSearchCondition condition,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<BookSummaryResponse> books = bookService.searchBooks(condition, pageable);
        return ApiResponse.ok(books, "도서 목록 조회 성공");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDetailResponse>> getBook(@PathVariable Long id) {
        BookDetailResponse book = bookService.getBookDetail(id);
        return ApiResponse.ok(book, "도서 상세 조회 성공");
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> createBook(@Valid @RequestBody BookCreateRequest request) {
        BookSummaryResponse response = bookService.createBook(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ApiResponse.created(null, location, "도서 등록 성공");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookUpdateRequest request) {
        bookService.updateBook(id, request);
        return ApiResponse.ok(null, "도서 수정 성공");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ApiResponse.noContent("도서 삭제 성공");
    }
}
