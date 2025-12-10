package wsd.bookstore.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReissueRequest {

    @NotBlank(message = "Refresh Token은 필수입니다.")
    private String refreshToken;
}
