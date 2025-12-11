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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import wsd.bookstore.book.response.CategoryResponse;
import wsd.bookstore.book.request.CategoryRequest;
import wsd.bookstore.book.service.CategoryService;
import wsd.bookstore.common.response.ApiResponse;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CategoryResponse>>> getCategories(Pageable pageable) {
        Page<CategoryResponse> response = categoryService.getCategories(pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "카테고리 목록 조회 성공"));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategory(@PathVariable Long categoryId) {
        CategoryResponse response = categoryService.getCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success(response, "카테고리 상세 조회 성공"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> createCategory(@Valid @RequestBody CategoryRequest request) {
        categoryService.createCategory(request);
        return ResponseEntity.ok(ApiResponse.noContent("카테고리 등록 성공"));
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryRequest request) {
        categoryService.updateCategory(categoryId, request);
        return ResponseEntity.ok(ApiResponse.noContent("카테고리 수정 성공"));
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.noContent("카테고리 삭제 성공"));
    }
}
