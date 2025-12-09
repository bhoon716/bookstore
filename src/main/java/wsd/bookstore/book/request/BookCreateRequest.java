package wsd.bookstore.book.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookCreateRequest {

    @NotBlank(message = "ISBN is required")
    private String isbn13;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be non-negative")
    private Long price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must be non-negative")
    private Integer stockQuantity;

    private LocalDateTime publishedAt;

    private @NotNull(message = "Publisher ID is required") Long publisherId;

    private List<Long> authorIds;

    private List<Long> categoryIds;
}
