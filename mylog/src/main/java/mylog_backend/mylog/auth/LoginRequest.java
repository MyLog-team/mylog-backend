package mylog_backend.mylog.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import mylog_backend.mylog.user.User;

@Getter
public class LoginRequest {

    @NotBlank(message = "아이디를 입력해주세요.")
    private String loginId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @Builder
    public LoginRequest(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }

}
