package wsd.bookstore.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import wsd.bookstore.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
