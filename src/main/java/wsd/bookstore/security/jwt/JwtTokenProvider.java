package wsd.bookstore.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import wsd.bookstore.user.entity.UserRole;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final Duration ACCESS_TTL = Duration.ofMinutes(30L);
    private static final Duration REFRESH_TTL = Duration.ofDays(7L);

    @Value("${jwt.secret}")
    private String secret;
    private SecretKey secretKey;

    @PostConstruct
    void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccess(Long id, String email, UserRole role) {
        String jti = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiredAt = new Date(now + ACCESS_TTL.toMillis());

        return Jwts.builder()
                .subject(String.valueOf(id))
                .id(jti)
                .claim("email", email)
                .claim("role", role.name())
                .claim("type", "access")
                .issuedAt(issuedAt)
                .expiration(expiredAt)
                .signWith(secretKey, SIG.HS256)
                .compact();
    }

    public String generateRefresh(Long id) {
        String jti = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiredAt = new Date(now + REFRESH_TTL.toMillis());

        return Jwts.builder()
                .subject(String.valueOf(id))
                .id(jti)
                .claim("type", "refresh")
                .issuedAt(issuedAt)
                .expiration(expiredAt)
                .signWith(secretKey, SIG.HS256)
                .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            // 만료 토큰
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            // 서명 불일치, 형식 오류 등
            return false;
        }
    }

    public Long getUserId(String token) {
        Claims claims = parseClaims(token);
        return Long.valueOf(claims.getSubject());
    }

    public String getEmail(String accessToken) {
        validateAccessTokenType(accessToken);
        Claims claims = parseClaims(accessToken);
        return claims.get("email", String.class);
    }

    public String getRole(String accessToken) {
        validateAccessTokenType(accessToken);
        Claims claims = parseClaims(accessToken);
        return claims.get("role", String.class);
    }

    public String getType(String token) {
        Claims claims = parseClaims(token);
        return claims.get("type", String.class);
    }

    private void validateAccessTokenType(String token) {
        if (!"access".equals(getType(token))) {
            throw new JwtException("토큰 타입 불일치");
        }
    }

    public Authentication getAuthentication(String token) {
        if (!validateToken(token)) {
            throw new JwtException("유효하지 않은 토큰");
        }
        String email = getEmail(token);
        String role = getRole(token);

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
        UserDetails principal = User
                .withUsername(email)
                .password("")
                .authorities(authorities)
                .build();

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public long getExpiration(String token) {
        Date expiration = parseClaims(token).getExpiration();
        long now = new Date().getTime();
        return (expiration.getTime() - now);
    }
}
