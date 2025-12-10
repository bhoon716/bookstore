package wsd.bookstore.book.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import wsd.bookstore.book.entity.Book;

@Getter
@AllArgsConstructor
public class BookSummaryResponse {

    private Long id;
    private String isbn13;
    private String title;
    private Long price;
    private LocalDateTime publishedAt;
    private String publisherName;
    private List<String> authors;

    public static BookSummaryResponse from(Book book) {
        return new BookSummaryResponse(
                book.getId(),
                book.getIsbn13(),
                book.getTitle(),
                book.getPrice(),
                book.getPublishedAt(),
                book.getPublisher() != null ? book.getPublisher().getName() : null,
                book.getBookAuthors().stream()
                        .map(bookAuthor -> bookAuthor.getAuthor().getName())
                        .toList());
    }
}
