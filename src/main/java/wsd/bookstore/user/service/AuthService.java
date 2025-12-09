package wsd.bookstore.user.service;

import static wsd.bookstore.security.jwt.JwtConstant.LOGOUT_VALUE;
import static wsd.bookstore.security.jwt.JwtConstant.REDIS_BL_PREFIX;
import static wsd.bookstore.security.jwt.JwtConstant.REDIS_RT_PREFIX;
import static wsd.bookstore.security.jwt.JwtConstant.REFRESH_TOKEN_DURATION;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wsd.bookstore.common.error.CustomException;
import wsd.bookstore.common.error.ErrorCode;
import wsd.bookstore.common.redis.RedisService;
import wsd.bookstore.security.jwt.JwtTokenProvider;
import wsd.bookstore.user.entity.User;
import wsd.bookstore.user.repository.UserRepository;
import wsd.bookstore.user.request.LoginRequest;
import wsd.bookstore.user.request.ReissueRequest;
import wsd.bookstore.user.request.SignupRequest;
import wsd.bookstore.user.response.LoginResponse;
import wsd.bookstore.user.response.SignupResponse;
import wsd.bookstore.user.response.UserResponse;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = createUser(request, encodedPassword);
        User savedUser = userRepository.save(user);

        return new SignupResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getUsername());
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        return generateLoginResponse(user);
    }

    public void logout(String accessToken) {
        String email = jwtTokenProvider.getEmail(accessToken);
        redisService.deleteValues(REDIS_RT_PREFIX + email);

        long expiration = jwtTokenProvider.getExpiration(accessToken);
        redisService.setValues(REDIS_BL_PREFIX + accessToken, LOGOUT_VALUE, Duration.ofMillis(expiration));
    }

    public LoginResponse reissue(ReissueRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        Long userId = jwtTokenProvider.getUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        validateRefreshToken(user.getEmail(), refreshToken);

        return generateLoginResponse(user);
    }

    private User createUser(SignupRequest request, String encodedPassword) {
        return User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .username(request.getUsername())
                .role(request.getRole())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .build();
    }

    private LoginResponse generateLoginResponse(User user) {
        String accessToken = jwtTokenProvider.generateAccess(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.generateRefresh(user.getId());

        redisService.setValues(REDIS_RT_PREFIX + user.getEmail(), refreshToken, REFRESH_TOKEN_DURATION);

        UserResponse userResponse = new UserResponse(user.getId(), user.getEmail(), user.getUsername(),
                user.getRole().name());
        return new LoginResponse(
                accessToken,
                refreshToken,
                userResponse);
    }

    private void validateRefreshToken(String email, String refreshToken) {
        String storedRefreshToken = redisService.getValues(REDIS_RT_PREFIX + email);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }
}
