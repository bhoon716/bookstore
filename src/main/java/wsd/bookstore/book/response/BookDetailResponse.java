package wsd.bookstore.book.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import wsd.bookstore.book.entity.Book;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@AllArgsConstructor
@Schema(description = "도서 상세 정보 응답 DTO")
public class BookDetailResponse {

        @Schema(description = "도서 ID", example = "1")
        private Long id;

        @Schema(description = "ISBN (13자리)", example = "9791162244246")
        private String isbn13;

        @Schema(description = "도서 제목", example = "Effective Java")
        private String title;

        @Schema(description = "도서 설명", example = "자바 프로그래밍 언어의 모범 사례를 담은 책")
        private String description;

        @Schema(description = "가격 (원)", example = "36000")
        private Long price;

        @Schema(description = "재고 수량", example = "100")
        private Integer stockQuantity;

        @Schema(description = "출판일", example = "2018-11-01T00:00:00")
        private LocalDateTime publishedAt;

        @Schema(description = "출판사 이름", example = "인사이트")
        private String publisherName;

        @Schema(description = "저자 목록", example = "[\"조슈아 블로크\"]")
        private List<String> authors;

        @Schema(description = "카테고리 목록", example = "[\"컴퓨터/IT\"]")
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
