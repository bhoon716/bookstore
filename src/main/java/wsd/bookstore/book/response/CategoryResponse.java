package wsd.bookstore.book.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wsd.bookstore.book.entity.Category;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@AllArgsConstructor
@Schema(description = "카테고리 정보 응답 DTO")
public class CategoryResponse {

    @Schema(description = "카테고리 ID", example = "1")
    private Long id;

    @Schema(description = "카테고리 이름", example = "컴퓨터/IT")
    private String name;

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName());
    }
}
