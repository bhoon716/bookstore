package wsd.bookstore.book.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wsd.bookstore.common.audit.BaseEntity;

@Getter
@Entity
@Table(name = "authors")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Author extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String bio;

    public Author(String name, String bio) {
        this.name = name;
        this.bio = bio;
    }
}
