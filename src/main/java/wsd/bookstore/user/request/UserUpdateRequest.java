package wsd.bookstore.user.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor
public class UserUpdateRequest {
    @Length(min = 9, max = 20, message = "비밀번호는 최소 9자, 최대 20자 입니다.")
    private String password;
    @Length(min = 2, max = 10, message = "이름은 2자 이상 10자 이하로 입력해주세요.")
    private String username;
    @Length(max = 50, message = "주소는 50자 이하로 입력해주세요.")
    private String address;
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678)")
    private String phoneNumber;
}
