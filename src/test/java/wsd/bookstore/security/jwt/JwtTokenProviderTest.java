package wsd.bookstore.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import wsd.bookstore.user.entity.UserRole;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    private final String secret = "v3rY5ecReTk3yF0rJwTTe5t1nGv3rY5ecReTk3yF0rJwTTe5t1nG";
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTokenProvider, "secret", secret);
        ReflectionTestUtils.setField(jwtTokenProvider, "accessExpiration", 1800000L);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshExpiration", 604800000L);
        ReflectionTestUtils.invokeMethod(jwtTokenProvider, "init");
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Nested
    @DisplayName("액세스 토큰 생성 테스트")
    class GenerateAccessTest {

        @Test
        @DisplayName("성공: 액세스 토큰을 정상적으로 생성해야 한다")
        void success() {
            // given
            Long id = 1L;
            String email = "test@test.com";
            UserRole role = UserRole.USER;

            // when
            String token = jwtTokenProvider.generateAccess(id, email, role);

            // then
            assertThat(token).isNotNull();
            assertThat(jwtTokenProvider.validateToken(token)).isTrue();
            assertThat(jwtTokenProvider.getType(token)).isEqualTo(JwtConstant.ACCESS_TOKEN_TYPE);
        }
    }

    @Nested
    @DisplayName("리프레시 토큰 생성 테스트")
    class GenerateRefreshTest {

        @Test
        @DisplayName("성공: 리프레시 토큰을 정상적으로 생성해야 한다")
        void success() {
            // given
            Long id = 1L;

            // when
            String token = jwtTokenProvider.generateRefresh(id);

            // then
            assertThat(token).isNotNull();
            assertThat(jwtTokenProvider.validateToken(token)).isTrue();
            assertThat(jwtTokenProvider.getType(token)).isEqualTo(JwtConstant.REFRESH_TOKEN_TYPE);
        }
    }

    @Nested
    @DisplayName("토큰 검증 테스트")
    class ValidateTokenTest {

        @Test
        @DisplayName("성공: 유효한 토큰은 true를 반환해야 한다")
        void success() {
            // given
            String token = jwtTokenProvider.generateAccess(1L, "test@test.com", UserRole.USER);

            // when
            boolean result = jwtTokenProvider.validateToken(token);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("실패: 만료된 토큰은 false를 반환해야 한다")
        void fail_expired() {
            // given
            Date now = new Date();
            Date past = new Date(now.getTime() - 1000); // 1 second ago
            String token = Jwts.builder()
                    .expiration(past)
                    .signWith(secretKey)
                    .compact();

            // when
            boolean result = jwtTokenProvider.validateToken(token);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("실패: 잘못된 서명의 토큰은 false를 반환해야 한다")
        void fail_invalidSignature() {
            // given
            String otherSecret = "oTh3rS3cR3tK3yF0rT3st1nGoTh3rS3cR3tK3yF0rT3st1nG";
            SecretKey otherKey = Keys.hmacShaKeyFor(otherSecret.getBytes(StandardCharsets.UTF_8));
            String token = Jwts.builder()
                    .signWith(otherKey)
                    .compact();

            // when
            boolean result = jwtTokenProvider.validateToken(token);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("실패: 잘못된 형식의 토큰은 false를 반환해야 한다")
        void fail_malformed() {
            // given
            String token = "invalid.token.format";

            // when
            boolean result = jwtTokenProvider.validateToken(token);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("토큰 정보 추출 테스트")
    class GetClaimsTest {

        @Test
        @DisplayName("성공: 토큰에서 사용자 ID를 추출해야 한다")
        void success_getUserId() {
            // given
            Long id = 1L;
            String token = jwtTokenProvider.generateAccess(id, "test@test.com", UserRole.USER);

            // when
            Long result = jwtTokenProvider.getUserId(token);

            // then
            assertThat(result).isEqualTo(id);
        }

        @Test
        @DisplayName("성공: 토큰에서 이메일을 추출해야 한다")
        void success_getEmail() {
            // given
            String email = "test@test.com";
            String token = jwtTokenProvider.generateAccess(1L, email, UserRole.USER);

            // when
            String result = jwtTokenProvider.getEmail(token);

            // then
            assertThat(result).isEqualTo(email);
        }

        @Test
        @DisplayName("성공: 토큰에서 권한 정보를 추출해야 한다")
        void success_getRole() {
            // given
            UserRole role = UserRole.ADMIN;
            String token = jwtTokenProvider.generateAccess(1L, "test@test.com", role);

            // when
            String result = jwtTokenProvider.getRole(token);

            // then
            assertThat(result).isEqualTo(role.name());
        }

        @Test
        @DisplayName("실패: 액세스 토큰이 아닌 토큰에서 이메일 추출 시 예외가 발생해야 한다")
        void fail_getEmail_wrongType() {
            // given
            String token = jwtTokenProvider.generateRefresh(1L);

            // when & then
            assertThatThrownBy(() -> jwtTokenProvider.getEmail(token))
                    .isInstanceOf(JwtException.class)
                    .hasMessage("토큰 타입 불일치");
        }
    }

    @Nested
    @DisplayName("Authentication 객체 생성 테스트")
    class GetAuthenticationTest {

        @Test
        @DisplayName("성공: 토큰으로 Authentication 객체를 생성해야 한다")
        void success() {
            // given
            String email = "test@test.com";
            String token = jwtTokenProvider.generateAccess(1L, email, UserRole.USER);

            // when
            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            // then
            assertThat(authentication).isNotNull();
            assertThat(authentication.getName()).isEqualTo(email);
            assertThat(authentication.getAuthorities()).hasSize(1);
            assertThat(authentication.getAuthorities().iterator().next().getAuthority()).isEqualTo("USER");
        }

        @Test
        @DisplayName("실패: 유효하지 않은 토큰으로 Authentication 생성 시 예외가 발생해야 한다")
        void fail_invalidToken() {
            // given
            String token = "invalid.token";

            // when & then
            assertThatThrownBy(() -> jwtTokenProvider.getAuthentication(token))
                    .isInstanceOf(JwtException.class)
                    .hasMessage("유효하지 않은 토큰");
        }
    }
}
