package wsd.bookstore.book.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@NoArgsConstructor
@Schema(description = "저자 등록 요청 DTO")
public class AuthorRequest {

    @NotBlank(message = "작가 이름은 필수입니다.")
    @Schema(description = "작가 이름", example = "조슈아 블로크")
    private String name;

    @Schema(description = "작가 설명", example = "Java 언어의 아키텍트이자 Effective Java의 저자")
    private String bio;

    public AuthorRequest(String name, String bio) {
        this.name = name;
        this.bio = bio;
    }
}
