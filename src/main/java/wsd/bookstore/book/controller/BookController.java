package wsd.bookstore.book.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wsd.bookstore.book.request.BookSearchCondition;
import wsd.bookstore.book.response.BookResponse;
import wsd.bookstore.book.service.BookService;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<PagedModel<BookResponse>> searchBooks(
            @ModelAttribute BookSearchCondition condition,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<BookResponse> response = bookService.searchBooks(condition, pageable);
        return ResponseEntity.ok(new PagedModel<>(response));
    }
}
