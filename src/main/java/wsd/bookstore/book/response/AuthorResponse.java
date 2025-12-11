package wsd.bookstore.book.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wsd.bookstore.book.entity.Author;

@Getter
@AllArgsConstructor
public class AuthorResponse {

    private Long id;
    private String name;
    private String bio;

    public static AuthorResponse from(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName(),
                author.getBio());
    }
}
