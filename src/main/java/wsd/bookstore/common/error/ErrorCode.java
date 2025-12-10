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
    NOT_FOUND_FAVORITE("40403", "등록되지 않은 즐겨찾기입니다", HttpStatus.NOT_FOUND),
    NOT_FOUND_WISHLIST("40404", "위시리스트에 등록되지 않은 도서입니다", HttpStatus.NOT_FOUND),
    NOT_FOUND_BOOK("40410", "도서를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    NOT_FOUND_PUBLISHER("40411", "출판사를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    NOT_FOUND_AUTHOR("40412", "저자를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    NOT_FOUND_CATEGORY("40413", "카테고리를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    NOT_FOUND_ORDER("40414", "주문을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    NOT_FOUND_CART_ITEM("40420", "장바구니 상품을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    INVALID_CART_STATUS("40002", "유효하지 않은 장바구니 상태입니다", HttpStatus.BAD_REQUEST),
    NOT_ENOUGH_STOCK("40003", "재고가 부족합니다", HttpStatus.BAD_REQUEST),

    // 405 Method Not Allowed
    NOT_ALLOWED_METHOD("40500", "허용되지 않은 요청 메서드", HttpStatus.METHOD_NOT_ALLOWED),

    // 409 Conflict
    CONFLICT("40900", "요청이 서버의 현재 상태와 충돌함", HttpStatus.CONFLICT),
    DUPLICATE_EMAIL("40901", "이미 사용 중인 이메일입니다", HttpStatus.CONFLICT),
    DUPLICATE_BOOK("40902", "이미 존재하는 도서입니다", HttpStatus.CONFLICT),
    DUPLICATE_FAVORITE("40903", "이미 즐겨찾기에 등록된 도서입니다", HttpStatus.CONFLICT),
    DUPLICATE_WISHLIST("40904", "이미 위시리스트에 등록된 도서입니다", HttpStatus.CONFLICT),

    // 500 Internal Server Error
    INTERNAL_ERROR("50000", "내부 서버 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final String code;
    private final String message;
    private final HttpStatus status;
}
