package mylog_backend.mylog.preference;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "사용자 선호 응답 DTO")
public class PreferenceResponse {

    @Schema(description = "응답 성공 메시지", example = "선호 태그 요청 성공")
    private String message;

    @Schema(description = "선호 태그1 아이디", example = "1")
    private Integer preferenceId1;
    @Schema(description = "선호 태그2 아이디", example = "2")
    private Integer preferenceId2;

    @Builder
    public PreferenceResponse(String message, Integer preferenceId1, Integer preferenceId2) {
        this.message = message;
        this.preferenceId1 = preferenceId1;
        this.preferenceId2 = preferenceId2;
    }

    /**
     * 태그를 성공적으로 입력시 사용
     * @param message : 성공 메시지
     * @param preferenceId1 : 선호태그1번의 아이디
     * @param preferenceId2 : 선호태그2번의 아이디
     * @return : of 정적 메서드로 응답 변환
     */
    public static PreferenceResponse of(String message, Integer preferenceId1, Integer preferenceId2) {
        return PreferenceResponse.builder()
                .message(message)
                .preferenceId1(preferenceId1)
                .preferenceId2(preferenceId2)
                .build();
    }


}
