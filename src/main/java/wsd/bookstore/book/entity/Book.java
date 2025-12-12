package wsd.bookstore.book.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import wsd.bookstore.common.audit.BaseEntity;
import wsd.bookstore.common.error.CustomException;
import wsd.bookstore.common.error.ErrorCode;

@Getter
@Entity
@Table(name = "books")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE books SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Check(constraints = "price >= 0 AND stock_quantity >= 0")
public class Book extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20, unique = true)
    private String isbn13;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Long price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "published_at", nullable = false)
    private LocalDateTime publishedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id", nullable = false)
    private Publisher publisher;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<BookAuthor> bookAuthors = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<BookCategory> bookCategories = new ArrayList<>();

    @Builder
    private Book(String isbn13, String title, String description, Long price,
            Integer stockQuantity, LocalDateTime publishedAt, Publisher publisher) {
        this.isbn13 = isbn13;
        this.title = title;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.publishedAt = publishedAt;
        this.publisher = publisher;
    }

    public void addAuthor(Author author) {
        this.bookAuthors.add(new BookAuthor(this, author));
    }

    public void addCategory(Category category) {
        this.bookCategories.add(new BookCategory(this, category));
    }

    public void updateBasicInfo(String title, String description, Long price,
            Integer stockQuantity, LocalDateTime publishedAt) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.publishedAt = publishedAt;
    }

    public void updatePublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public void clearAuthors() {
        this.bookAuthors.clear();
    }

    public void clearCategories() {
        this.bookCategories.clear();
    }

    public void decreaseStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new CustomException(ErrorCode.NOT_ENOUGH_STOCK);
        }
        this.stockQuantity = restStock;
    }

    public void increaseStock(int quantity) {
        this.stockQuantity += quantity;
    }
}
