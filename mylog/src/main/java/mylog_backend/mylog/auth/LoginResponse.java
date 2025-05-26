package mylog_backend.mylog.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "로그인 응답 DTO")
@Getter
@AllArgsConstructor
@Builder
public class LoginResponse {

    @Schema(description = "사용자 토큰")
    @NotBlank
    private String accessToken;

    @Schema(description = "사용자 이름")
    @NotBlank
    private String userName;

    @Schema(description = "권한 종류")
    @NotBlank
    private String grantType;

    @Schema(description = "리프레시 토큰")
    @NotBlank
    private String refreshToken;
}