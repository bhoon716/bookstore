package wsd.bookstore.book.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthorRequest {

    @NotBlank(message = "작가 이름은 필수입니다.")
    private String name;

    private String bio;

    public AuthorRequest(String name, String bio) {
        this.name = name;
        this.bio = bio;
    }
}
