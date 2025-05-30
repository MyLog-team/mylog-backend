package mylog_backend.mylog.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignupResponse {

    @Schema(description = "응답 메시지")
    private String message;

    @Schema(description = "사용자 아이디")
    @NotBlank
    private Long userId;

    @Builder
    public SignupResponse(String message, Long userId) {
        this.message = message;
        this.userId = userId;
    }


    /**
     * 회원가입 성공시 반환
     * @param message : 응답 메시지
     * @param userId : DB에 저장된 사용자 아이디
     * @return
     */
    public static SignupResponse of(String message, Long userId) {
        return SignupResponse.builder()
                .message(message)
                .userId(userId)
                .build();
    }


}
