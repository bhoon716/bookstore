package wsd.bookstore.security.jwt;

import java.time.Duration;

public class JwtConstant {
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofMinutes(30);
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(7);
    public static final String REDIS_RT_PREFIX = "RT:";
    public static final String REDIS_BL_PREFIX = "BL:";
    public static final String CLAIM_EMAIL = "email";
    public static final String CLAIM_ROLE = "role";
    public static final String CLAIM_TYPE = "type";
    public static final String ACCESS_TOKEN_TYPE = "access";
    public static final String REFRESH_TOKEN_TYPE = "refresh";
    public static final String LOGOUT_VALUE = "logout";

    private JwtConstant() {
    }
}
