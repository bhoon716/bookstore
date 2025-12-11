package wsd.bookstore.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import wsd.bookstore.security.auth.CustomUserDetailsService;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("doFilterInternal() 테스트")
    class DoFilterInternalTest {

        @Test
        @DisplayName("성공: 헤더가 없으면 다음 필터로 진행한다")
        void success_noHeader() throws ServletException, IOException {
            // given
            given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn(null);

            // when
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // then
            verify(filterChain).doFilter(request, response);
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }

        @Test
        @DisplayName("성공: Bearer 토큰이 아니면 다음 필터로 진행한다")
        void success_invalidPrefix() throws ServletException, IOException {
            // given
            given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn("Basic token");

            // when
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // then
            verify(filterChain).doFilter(request, response);
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }

        @Test
        @DisplayName("성공: 토큰이 유효하지 않으면 다음 필터로 진행한다")
        void success_invalidToken() throws ServletException, IOException {
            // given
            String token = "invalidToken";
            given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn("Bearer " + token);
            given(jwtTokenProvider.validateToken(token)).willReturn(false);

            // when
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // then
            verify(filterChain).doFilter(request, response);
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }

        @Test
        @DisplayName("성공: 유효한 토큰일 경우 인증 정보를 설정하고 다음 필터로 진행한다")
        void success_validToken() throws ServletException, IOException {
            // given
            String token = "validToken";
            String email = "test@test.com";
            UserDetails userDetails = mock(UserDetails.class);

            given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn("Bearer " + token);
            given(jwtTokenProvider.validateToken(token)).willReturn(true);
            given(jwtTokenProvider.getEmail(token)).willReturn(email);
            given(customUserDetailsService.loadUserByUsername(email)).willReturn(userDetails);
            given(userDetails.getAuthorities()).willReturn(Collections.emptyList());

            // when
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // then
            verify(filterChain).doFilter(request, response);
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
            assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(userDetails);
        }
    }
}
