package wsd.bookstore.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import wsd.bookstore.common.error.ErrorCode;

@Getter
@JsonInclude(Include.NON_NULL)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    public Boolean success;
    public String code;
    public String message;
    public T data;

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, "success", message, data);
    }

    public static <T> ApiResponse<String> fail(ErrorCode errorCode, String detail) {
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), detail);
    }
}
