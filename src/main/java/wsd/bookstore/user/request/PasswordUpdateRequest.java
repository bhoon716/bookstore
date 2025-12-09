package wsd.bookstore.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor
public class PasswordUpdateRequest {

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Length(min = 9, max = 20, message = "비밀번호는 최소 9자, 최대 20자 입니다.")
    private String password;
}
