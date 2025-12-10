package wsd.bookstore.user.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wsd.bookstore.common.response.ApiResponse;
import wsd.bookstore.user.request.LoginRequest;
import wsd.bookstore.user.request.ReissueRequest;
import wsd.bookstore.user.request.SignupRequest;
import wsd.bookstore.user.response.LoginResponse;
import wsd.bookstore.user.response.SignupResponse;
import wsd.bookstore.user.response.UserResponse;
import wsd.bookstore.user.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private static final String AUTHORIZATION_HEADER = HttpHeaders.AUTHORIZATION;
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    private static final int COOKIE_MAX_AGE = 7 * 24 * 60 * 60; // 7 days

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = authService.signup(request);
        return ResponseEntity.ok(ApiResponse.success(response, "회원가입 성공"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse httpServletResponse) {
        LoginResponse response = authService.login(request);

        setAccessTokenHeader(httpServletResponse, response.getAccessToken());
        addRefreshTokenCookie(httpServletResponse, response.getRefreshToken());

        return ResponseEntity.ok(ApiResponse.success(response.getUser(), "로그인 성공"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(AUTHORIZATION_HEADER) String accessToken,
            HttpServletResponse httpServletResponse) {
        authService.logout(accessToken.substring(BEARER_PREFIX.length()));
        removeRefreshTokenCookie(httpServletResponse);
        return ResponseEntity.ok(ApiResponse.success(null, "로그아웃 성공"));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<UserResponse>> reissue(
            @CookieValue(REFRESH_TOKEN_COOKIE_NAME) String refreshToken,
            HttpServletResponse httpServletResponse) {
        LoginResponse response = authService.reissue(new ReissueRequest(refreshToken));

        setAccessTokenHeader(httpServletResponse, response.getAccessToken());
        addRefreshTokenCookie(httpServletResponse, response.getRefreshToken());

        return ResponseEntity.ok(ApiResponse.success(response.getUser(), "토큰 재발급 성공"));
    }

    private void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken);
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_MAX_AGE);
        response.addCookie(cookie);
    }

    private void removeRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
