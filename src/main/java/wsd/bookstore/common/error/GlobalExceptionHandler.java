package wsd.bookstore.common.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wsd.bookstore.common.response.ApiResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<String>> handleCustomException(CustomException exception) {
        ErrorCode error = exception.getErrorCode();
        String code = error.getCode();
        String message = error.getMessage();
        HttpStatus status = error.getStatus();
        String detail = exception.getDetail();

        if (detail == null) {
            log.error("[ERROR] code={}, message={}, status={}", code, message, status);
        } else {
            log.error("[ERROR] code={}, message={}, detail={}, status={}", code, message, detail, status);
        }

        return ResponseEntity.status(status).body(ApiResponse.fail(error, detail));
    }
}
