package wsd.bookstore.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 400 Bad Request
    BAD_REQUEST("40000", "잘못된 요청", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD("40001", "비밀번호가 일치하지 않습니다", HttpStatus.BAD_REQUEST),

    // 401 Unauthorized
    UNAUTHORIZED("40100", "권한 없음", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("40101", "유효하지 않은 토큰입니다", HttpStatus.UNAUTHORIZED),

    // 403 Forbidden
    FORBIDDEN("40300", "요청 거부됨", HttpStatus.FORBIDDEN),

    // 404 Not Found
    NOT_FOUND("40400", "자원을 찾을 수 없음", HttpStatus.NOT_FOUND),
    NOT_FOUND_USER("40401", "유저를 찾을 수 없음", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND("40402", "사용자를 찾을 수 없습니다", HttpStatus.NOT_FOUND),

    // 405 Method Not Allowed
    NOT_ALLOWED_METHOD("40500", "허용되지 않는 요청 메서드", HttpStatus.METHOD_NOT_ALLOWED),

    // 409 Conflict
    CONFLICT("40900", "요청이 서버의 현재 상태와 충돌함", HttpStatus.CONFLICT),
    DUPLICATE_EMAIL("40901", "이미 사용 중인 이메일입니다", HttpStatus.CONFLICT),

    // 500 Internal Server Error
    INTERNAL_ERROR("50000", "내부 서버 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final String code;
    private final String message;
    private final HttpStatus status;
}
