package wsd.bookstore.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import wsd.bookstore.common.audit.BaseEntity;

@Entity
@Getter

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?")
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false)
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Builder
    private User(String email, String password, String username, UserRole role, String address, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.role = role;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public void update(String password, String username, String address, String phoneNumber) {
        this.password = password;
        this.username = username;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
}
