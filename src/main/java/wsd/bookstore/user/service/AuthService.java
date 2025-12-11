package wsd.bookstore.user.service;

import static wsd.bookstore.security.jwt.JwtConstant.LOGOUT_VALUE;
import static wsd.bookstore.security.jwt.JwtConstant.REDIS_BL_PREFIX;
import static wsd.bookstore.security.jwt.JwtConstant.REDIS_RT_PREFIX;
import static wsd.bookstore.security.jwt.JwtConstant.REFRESH_TOKEN_DURATION;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wsd.bookstore.common.error.CustomException;
import wsd.bookstore.common.error.ErrorCode;
import wsd.bookstore.common.redis.RedisService;
import wsd.bookstore.security.jwt.JwtTokenProvider;
import wsd.bookstore.user.entity.User;
import wsd.bookstore.user.entity.UserRole;
import wsd.bookstore.user.repository.UserRepository;
import wsd.bookstore.user.request.LoginRequest;
import wsd.bookstore.user.request.ReissueRequest;
import wsd.bookstore.user.request.SignupRequest;
import wsd.bookstore.user.response.LoginResponse;
import wsd.bookstore.user.response.SignupResponse;
import wsd.bookstore.user.response.UserResponse;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    public SignupResponse signup(SignupRequest request) {
        log.info("회원가입 요청: email={}", request.getEmail());
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = createUser(request, encodedPassword);
        User savedUser = userRepository.save(user);

        log.info("회원가입 완료: userId={}", savedUser.getId());
        return SignupResponse.from(savedUser);
    }

    public LoginResponse login(LoginRequest request) {
        log.info("로그인 요청: email={}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        log.info("로그인 완료: userId={}", user.getId());
        return generateLoginResponse(user);
    }

    public void logout(String accessToken) {
        String email = jwtTokenProvider.getEmail(accessToken);
        log.info("로그아웃 요청: email={}", email);
        redisService.deleteValues(REDIS_RT_PREFIX + email);

        long expiration = jwtTokenProvider.getExpiration(accessToken);
        redisService.setValues(REDIS_BL_PREFIX + accessToken, LOGOUT_VALUE, Duration.ofMillis(expiration));
        log.info("로그아웃 완료: email={}", email);
    }

    public LoginResponse reissue(ReissueRequest request) {
        log.info("토큰 재발급 요청");
        String refreshToken = request.getRefreshToken();

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        Long userId = jwtTokenProvider.getUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        validateRefreshToken(user.getEmail(), refreshToken);

        log.info("토큰 재발급 완료: userId={}", userId);
        return generateLoginResponse(user);
    }

    private User createUser(SignupRequest request, String encodedPassword) {
        return User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .username(request.getUsername())
                .role(UserRole.USER)
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .build();
    }

    private LoginResponse generateLoginResponse(User user) {
        String accessToken = jwtTokenProvider.generateAccess(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.generateRefresh(user.getId());

        redisService.setValues(REDIS_RT_PREFIX + user.getEmail(), refreshToken, REFRESH_TOKEN_DURATION);

        UserResponse userResponse = UserResponse.from(user);
        return LoginResponse.of(accessToken, refreshToken, userResponse);
    }

    private void validateRefreshToken(String email, String refreshToken) {
        String storedRefreshToken = redisService.getValues(REDIS_RT_PREFIX + email);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }
}
