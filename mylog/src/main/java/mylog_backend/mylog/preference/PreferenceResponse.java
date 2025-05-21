package mylog_backend.mylog.preference;

import lombok.Builder;

public class PreferenceResponse {

    private String message;

    private Integer preferenceId1;
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
