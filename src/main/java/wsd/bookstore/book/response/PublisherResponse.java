package wsd.bookstore.book.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wsd.bookstore.book.entity.Publisher;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@AllArgsConstructor
@Schema(description = "출판사 정보 응답 DTO")
public class PublisherResponse {

    @Schema(description = "출판사 ID", example = "1")
    private Long id;

    @Schema(description = "출판사 이름", example = "인사이트")
    private String name;

    public static PublisherResponse from(Publisher publisher) {
        return new PublisherResponse(
                publisher.getId(),
                publisher.getName());
    }
}
