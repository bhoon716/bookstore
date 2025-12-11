package wsd.bookstore.book.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wsd.bookstore.book.entity.Publisher;

@Getter
@AllArgsConstructor
public class PublisherResponse {

    private Long id;
    private String name;

    public static PublisherResponse from(Publisher publisher) {
        return new PublisherResponse(
                publisher.getId(),
                publisher.getName());
    }
}
