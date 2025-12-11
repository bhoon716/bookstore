package wsd.bookstore.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
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

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RedisService redisService;

    @Nested
    @DisplayName("회원가입 테스트")
    class SignupTest {

        @Test
        @DisplayName("성공: 회원가입이 성공적으로 완료되어야 한다")
        void success() {
            // given
            SignupRequest request = new SignupRequest("test@test.com", "password", "testName", "Seoul",
                    "010-1234-5678");
            String encodedPassword = "encodedPassword";
            User user = User.builder()
                    .email(request.getEmail())
                    .password(encodedPassword)
                    .username(request.getUsername())
                    .role(UserRole.USER)
                    .address(request.getAddress())
                    .phoneNumber(request.getPhoneNumber())
                    .build();
            ReflectionTestUtils.setField(user, "id", 1L);

            given(userRepository.existsByEmail(anyString())).willReturn(false);
            given(passwordEncoder.encode(anyString())).willReturn(encodedPassword);
            given(userRepository.save(any(User.class))).willReturn(user);

            // when
            SignupResponse response = authService.signup(request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getEmail()).isEqualTo(request.getEmail());
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("실패: 이미 존재하는 이메일로 가입 시도 시 예외가 발생해야 한다")
        void fail_duplicateEmail() {
            // given
            SignupRequest request = new SignupRequest("test@test.com", "password", "testName", "Seoul",
                    "010-1234-5678");
            given(userRepository.existsByEmail(anyString())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> authService.signup(request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_EMAIL);
        }
    }

    @Nested
    @DisplayName("로그인 테스트")
    class LoginTest {

        @Test
        @DisplayName("성공: 로그인이 성공적으로 완료되어야 한다")
        void success() {
            // given
            LoginRequest request = new LoginRequest("test@test.com", "password");
            User user = User.builder()
                    .email(request.getEmail())
                    .password("encodedPassword")
                    .role(UserRole.USER)
                    .build();
            ReflectionTestUtils.setField(user, "id", 1L);

            given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
            given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
            given(jwtTokenProvider.generateAccess(any(Long.class), anyString(), any(UserRole.class)))
                    .willReturn("accessToken");
            given(jwtTokenProvider.generateRefresh(any(Long.class))).willReturn("refreshToken");

            // when
            LoginResponse response = authService.login(request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo("accessToken");
            assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
            verify(redisService).setValues(anyString(), anyString(), any());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 사용자로 로그인 시도 시 예외가 발생해야 한다")
        void fail_userNotFound() {
            // given
            LoginRequest request = new LoginRequest("test@test.com", "password");
            given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_USER);
        }

        @Test
        @DisplayName("실패: 비밀번호 불일치 시 예외가 발생해야 한다")
        void fail_invalidPassword() {
            // given
            LoginRequest request = new LoginRequest("test@test.com", "password");
            User user = User.builder()
                    .email(request.getEmail())
                    .password("encodedPassword")
                    .role(UserRole.USER)
                    .build();
            ReflectionTestUtils.setField(user, "id", 1L);

            given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
            given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

            // when & then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PASSWORD);
        }
    }

    @Nested
    @DisplayName("로그아웃 테스트")
    class LogoutTest {

        @Test
        @DisplayName("성공: 로그아웃이 성공적으로 완료되어야 한다")
        void success() {
            // given
            String accessToken = "accessToken";
            String email = "test@test.com";
            long expiration = 1000L;

            given(jwtTokenProvider.getEmail(anyString())).willReturn(email);
            given(jwtTokenProvider.getExpiration(anyString())).willReturn(expiration);

            // when
            authService.logout(accessToken);

            // then
            verify(redisService).deleteValues(anyString());
            verify(redisService).setValues(anyString(), anyString(), any(Duration.class));
        }
    }

    @Nested
    @DisplayName("토큰 재발급 테스트")
    class ReissueTest {

        @Test
        @DisplayName("성공: 토큰 재발급이 성공적으로 완료되어야 한다")
        void success() {
            // given
            ReissueRequest request = new ReissueRequest("refreshToken");
            User user = User.builder()
                    .email("test@test.com")
                    .role(UserRole.USER)
                    .build();
            ReflectionTestUtils.setField(user, "id", 1L);

            given(jwtTokenProvider.validateToken(anyString())).willReturn(true);
            given(jwtTokenProvider.getUserId(anyString())).willReturn(user.getId());
            given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
            given(redisService.getValues(anyString())).willReturn("refreshToken");
            given(jwtTokenProvider.generateAccess(any(Long.class), anyString(), any(UserRole.class)))
                    .willReturn("newAccessToken");
            given(jwtTokenProvider.generateRefresh(any(Long.class))).willReturn("newRefreshToken");

            // when
            LoginResponse response = authService.reissue(request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo("newAccessToken");
            assertThat(response.getRefreshToken()).isEqualTo("newRefreshToken");
        }

        @Test
        @DisplayName("실패: 유효하지 않은 리프레시 토큰으로 재발급 시도 시 예외가 발생해야 한다")
        void fail_invalidToken() {
            // given
            ReissueRequest request = new ReissueRequest("invalidRefreshToken");
            given(jwtTokenProvider.validateToken(anyString())).willReturn(false);

            // when & then
            assertThatThrownBy(() -> authService.reissue(request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_TOKEN);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 사용자로 재발급 시도 시 예외가 발생해야 한다")
        void fail_userNotFound() {
            // given
            ReissueRequest request = new ReissueRequest("refreshToken");
            given(jwtTokenProvider.validateToken(anyString())).willReturn(true);
            given(jwtTokenProvider.getUserId(anyString())).willReturn(1L);
            given(userRepository.findById(any(Long.class))).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> authService.reissue(request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_USER);
        }

        @Test
        @DisplayName("실패: 저장된 리프레시 토큰과 불일치 시 예외가 발생해야 한다")
        void fail_tokenMismatch() {
            // given
            ReissueRequest request = new ReissueRequest("refreshToken");
            User user = User.builder()
                    .email("test@test.com")
                    .role(UserRole.USER)
                    .build();
            ReflectionTestUtils.setField(user, "id", 1L);

            given(jwtTokenProvider.validateToken(anyString())).willReturn(true);
            given(jwtTokenProvider.getUserId(anyString())).willReturn(user.getId());
            given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
            given(redisService.getValues(anyString())).willReturn("differentRefreshToken");

            // when & then
            assertThatThrownBy(() -> authService.reissue(request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_TOKEN);
        }
    }
}
