package wsd.bookstore.book.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import wsd.bookstore.book.entity.Book;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@AllArgsConstructor
@Schema(description = "도서 요약 정보 응답 DTO")
public class BookSummaryResponse {

    @Schema(description = "도서 ID", example = "1")
    private Long id;

    @Schema(description = "ISBN (13자리)", example = "9791162244246")
    private String isbn13;

    @Schema(description = "도서 제목", example = "Effective Java")
    private String title;

    @Schema(description = "가격 (원)", example = "36000")
    private Long price;

    @Schema(description = "출판일", example = "2018-11-01T00:00:00")
    private LocalDateTime publishedAt;

    @Schema(description = "출판사 이름", example = "인사이트")
    private String publisherName;

    @Schema(description = "저자 목록", example = "[\"조슈아 블로크\"]")
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
