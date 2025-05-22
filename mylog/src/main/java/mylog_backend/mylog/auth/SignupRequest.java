package mylog_backend.mylog.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mylog_backend.mylog.user.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignupRequest {

    @NotBlank(message = "아이디를 입력해주세요.")
    private String loginId;

    @NotBlank(message = "이름을 입력해주세요.")
    private String userName;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;


    @Builder
    public SignupRequest(String loginId, String userName, String password) {
        this.loginId = loginId;
        this.password = password;
        this.userName = userName;
    }

    public User toUser(String encodedPassword) {
        return User.builder()
                .loginId(loginId)
                .userName(userName)
                .password(encodedPassword) // 암호화된 패스워드를 할당
                .build();
    }

}
