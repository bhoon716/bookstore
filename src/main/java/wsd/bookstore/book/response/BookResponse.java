package wsd.bookstore.book.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class BookResponse {

    private Long id;
    private String title;
    @Setter
    private List<String> authorNames;
    private String publisherName;
    private Long price;
    private LocalDateTime publishedAt;

    @QueryProjection
    public BookResponse(Long id, String title, String publisherName, Long price, LocalDateTime publishedAt) {
        this.id = id;
        this.title = title;
        this.publisherName = publisherName;
        this.price = price;
        this.publishedAt = publishedAt;
    }
}
