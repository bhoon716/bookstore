package wsd.bookstore.book.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@NoArgsConstructor
@Schema(description = "카테고리 등록 요청 DTO")
public class CategoryRequest {

    @NotBlank(message = "카테고리 이름은 필수입니다.")
    @Schema(description = "카테고리 이름", example = "컴퓨터/IT")
    private String name;

    public CategoryRequest(String name) {
        this.name = name;
    }
}
