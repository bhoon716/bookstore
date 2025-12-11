package wsd.bookstore.common.error;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wsd.bookstore.common.response.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(
            HttpServletRequest request, CustomException e) {

        ErrorCode error = e.getErrorCode();
        Map<String, Object> details = detailOrNull(e.getDetail());

        log.error("[ERROR] code={}, message={}, path={}, detail={}",
                error.getCode(), error.getMessage(), request.getRequestURI(), e.getDetail(), e);

        return buildResponse(error, request.getRequestURI(), details);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            HttpServletRequest request, MethodArgumentNotValidException e) {

        Map<String, Object> details = new HashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(error -> details.put(error.getField(), error.getDefaultMessage()));

        log.warn("[WARN] Validation failed: path={}, details={}", request.getRequestURI(), details);

        return buildResponse(ErrorCode.INVALID_INPUT, request.getRequestURI(), details);
    }

    @ExceptionHandler({AuthorizationDeniedException.class, AccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            HttpServletRequest request, RuntimeException e) {

        log.warn("[WARN] Access denied: path={}, message={}", request.getRequestURI(), e.getMessage());

        return buildResponse(ErrorCode.FORBIDDEN, request.getRequestURI(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            HttpServletRequest request, Exception e) {

        log.error("[ERROR] Unhandled exception: path={}, exception={}",
                request.getRequestURI(), e.getClass().getSimpleName(), e);

        Map<String, Object> details = detailOrNull(e.getMessage());
        return buildResponse(ErrorCode.INTERNAL_ERROR, request.getRequestURI(), details);
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            ErrorCode error, String path, Map<String, Object> details) {
        return ResponseEntity
                .status(error.getStatus())
                .body(ErrorResponse.of(error, path, details));
    }

    private Map<String, Object> detailOrNull(String detailMessage) {
        return detailMessage == null ? null : Map.of("detail", detailMessage);
    }
}
