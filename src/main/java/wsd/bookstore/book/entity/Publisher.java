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
@Table(name = "publishers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Publisher extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    public Publisher(String name) {
        this.name = name;
    }

    public void update(String name) {
        this.name = name;
    }
}
