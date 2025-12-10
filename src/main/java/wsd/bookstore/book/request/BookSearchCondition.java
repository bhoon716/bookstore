package wsd.bookstore.book.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BookSearchCondition {

    private String keyword;
    private Long categoryId;
    private Long authorId;
    private Long publisherId;

    public BookSearchCondition(String keyword, Long categoryId, Long authorId, Long publisherId) {
        this.keyword = keyword;
        this.categoryId = categoryId;
        this.authorId = authorId;
        this.publisherId = publisherId;
    }
}
