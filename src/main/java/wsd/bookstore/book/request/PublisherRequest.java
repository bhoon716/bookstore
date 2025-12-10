package wsd.bookstore.book.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PublisherRequest {

    @NotBlank(message = "출판사 이름은 필수입니다.")
    private String name;

    public PublisherRequest(String name) {
        this.name = name;
    }
}
