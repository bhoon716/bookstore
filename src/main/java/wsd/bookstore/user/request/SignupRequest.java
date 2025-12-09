package wsd.bookstore.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import wsd.bookstore.user.entity.UserRole;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Length(min = 9, max = 20, message = "비밀번호는 최소 9자, 최대 20자 입니다.")
    private String password;

    @NotBlank(message = "이름은 필수입니다.")
    private String username;

    @NotNull(message = "역할은 필수입니다.")
    private UserRole role;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    @NotBlank(message = "전화번호는 필수입니다.")
    private String phoneNumber;
}
