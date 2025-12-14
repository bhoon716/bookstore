package wsd.bookstore.book.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wsd.bookstore.common.audit.BaseEntity;

@Getter
@Entity
@Table(name = "book_categories", indexes = {
        @Index(name = "idx_book_categories_book_id", columnList = "book_id"),
        @Index(name = "idx_book_categories_category_id", columnList = "category_id")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public BookCategory(Book book, Category category) {
        this.book = book;
        this.category = category;
    }
}
