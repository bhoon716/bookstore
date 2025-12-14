package wsd.bookstore.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@NoArgsConstructor
@Schema(description = "프로필 정보 수정 요청 DTO")
public class ProfileUpdateRequest {

    @NotBlank(message = "이름은 필수입니다.")
    @Length(min = 2, max = 10, message = "이름은 2자 이상 10자 이하로 입력해주세요.")
    @Schema(description = "이름 (2~10자)", example = "김철수")
    private String username;

    @NotBlank(message = "주소는 필수입니다.")
    @Length(max = 50, message = "주소는 50자 이하로 입력해주세요.")
    @Schema(description = "주소 (최대 50자)", example = "서울시 강남구 역삼동")
    private String address;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678)")
    @Schema(description = "전화번호 (010-XXXX-XXXX)", example = "010-5678-1234")
    private String phoneNumber;
}
