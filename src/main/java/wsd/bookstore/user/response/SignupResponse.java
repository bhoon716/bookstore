package wsd.bookstore.user.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import wsd.bookstore.user.entity.User;

@Getter
@AllArgsConstructor
public class SignupResponse {
    private Long userId;
    private String email;
    private String username;

    public static SignupResponse from(User user) {
        return new SignupResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername()
        );
    }
}
