package wsd.bookstore.book.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import wsd.bookstore.book.entity.Book;

@Getter
@AllArgsConstructor
public class BookDetailResponse {

    private Long id;
    private String isbn13;
    private String title;
    private String description;
    private Long price;
    private Integer stockQuantity;
    private LocalDateTime publishedAt;
    private String publisherName;
    private List<String> authors;
    private List<String> categories;

    public static BookDetailResponse from(Book book) {
        return new BookDetailResponse(
                book.getId(),
                book.getIsbn13(),
                book.getTitle(),
                book.getDescription(),
                book.getPrice(),
                book.getStockQuantity(),
                book.getPublishedAt(),
                book.getPublisher().getName(),
                book.getBookAuthors().stream()
                        .map(bookAuthor -> bookAuthor.getAuthor().getName())
                        .toList(),
                book.getBookCategories().stream()
                        .map(bookCategory -> bookCategory.getCategory().getName())
                        .toList());
    }
}
