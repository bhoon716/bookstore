package wsd.bookstore.book.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import wsd.bookstore.book.response.CategoryResponse;
import wsd.bookstore.book.request.CategoryRequest;
import wsd.bookstore.book.service.CategoryService;
import wsd.bookstore.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "카테고리 관리 API")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "카테고리 목록 조회", description = "전체 카테고리 목록을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "카테고리 목록 조회 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "카테고리 목록 조회 성공",
                "payload": [
                    {
                        "categoryId": 1,
                        "name": "IT/컴퓨터"
                    },
                     {
                        "categoryId": 2,
                        "name": "소설"
                    }
                ]
            }
            """)))
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories() {
        List<CategoryResponse> response = categoryService.getCategories();
        return ApiResponse.ok(response, "카테고리 목록 조회 성공");
    }

    @GetMapping("/{categoryId}")
    @Operation(summary = "카테고리 상세 조회", description = "카테고리 ID로 상세 정보를 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "카테고리 상세 조회 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "카테고리 상세 조회 성공",
                "payload": {
                    "categoryId": 1,
                    "name": "IT/컴퓨터"
                }
            }
            """)))
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategory(@PathVariable Long categoryId) {
        CategoryResponse response = categoryService.getCategory(categoryId);
        return ApiResponse.ok(response, "카테고리 상세 조회 성공");
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "카테고리 등록", description = "관리자가 새로운 카테고리를 등록합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "생성 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "카테고리 등록 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "카테고리 등록 성공",
                "payload": null
            }
            """)))
    public ResponseEntity<ApiResponse<Void>> createCategory(@Valid @RequestBody CategoryRequest request) {
        Long categoryId = categoryService.createCategory(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(categoryId)
                .toUri();
        return ApiResponse.created(null, location, "카테고리 등록 성공");
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "카테고리 수정", description = "관리자가 카테고리 정보를 수정합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "카테고리 수정 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "카테고리 수정 성공",
                "payload": null
            }
            """)))
    public ResponseEntity<ApiResponse<Void>> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryRequest request) {
        categoryService.updateCategory(categoryId, request);
        return ApiResponse.ok(null, "카테고리 수정 성공");
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "카테고리 삭제", description = "관리자가 카테고리를 삭제합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "삭제 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "카테고리 삭제 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "카테고리 삭제 성공",
                "payload": null
            }
            """)))
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ApiResponse.noContent("카테고리 삭제 성공");
    }
}
