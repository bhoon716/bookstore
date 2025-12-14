package wsd.bookstore.book.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wsd.bookstore.book.entity.Author;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@AllArgsConstructor
@Schema(description = "저자 정보 응답 DTO")
public class AuthorResponse {

    @Schema(description = "저자 ID", example = "1")
    private Long id;

    @Schema(description = "저자 이름", example = "조슈아 블로크")
    private String name;

    @Schema(description = "저자 설명", example = "Java 언어의 아키텍트")
    private String bio;

    public static AuthorResponse from(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName(),
                author.getBio());
    }
}
