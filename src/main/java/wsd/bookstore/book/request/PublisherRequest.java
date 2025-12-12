package wsd.bookstore.book.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@NoArgsConstructor
@Schema(description = "출판사 등록 요청 DTO")
public class PublisherRequest {

    @NotBlank(message = "출판사 이름은 필수입니다.")
    @Schema(description = "출판사 이름", example = "인사이트")
    private String name;

    public PublisherRequest(String name) {
        this.name = name;
    }
}
