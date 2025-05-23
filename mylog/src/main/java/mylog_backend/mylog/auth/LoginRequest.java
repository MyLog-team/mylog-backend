package mylog_backend.mylog.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import mylog_backend.mylog.user.User;

@Schema(description = "로그인 요청 DTO")
@Getter
public class LoginRequest {

    @Schema(description = "사용자 아이디", example = "daiseek123")
    @NotBlank(message = "아이디를 입력해주세요.")
    private String loginId;

    @Schema(description = "사용자 비밀번호", example = "1234")
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @Builder
    public LoginRequest(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }

}
