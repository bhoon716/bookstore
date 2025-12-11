package wsd.bookstore.book.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "카테고리 이름은 필수입니다.")
    private String name;

    public CategoryRequest(String name) {
        this.name = name;
    }
}
