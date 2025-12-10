package wsd.bookstore.user.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private UserResponse user;

    public static LoginResponse of(String accessToken, String refreshToken, UserResponse user) {
        return new LoginResponse(accessToken, refreshToken, user);
    }
}
