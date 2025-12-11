package wsd.bookstore.book.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wsd.bookstore.book.entity.Category;

@Getter
@AllArgsConstructor
public class CategoryResponse {

    private Long id;
    private String name;

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName());
    }
}
