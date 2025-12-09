package wsd.bookstore.user.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import wsd.bookstore.user.entity.User;

@Getter
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String email;
    private String username;
    private String role;

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole().name());
    }
}
