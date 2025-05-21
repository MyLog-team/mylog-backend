package mylog_backend.mylog.diary;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DiaryResponse {

    private String message;

    private Long diaryId;

    /**
     * 일기 응답 DTO 생성자
     * 일기 관련 로직후 이용됨
     * 로직이 다양해질수록 확장성을 띄도록 설계
     * @param message
     * @param diaryId
     */
    @Builder
    public DiaryResponse(String message, Long diaryId) {
        this.diaryId = diaryId;
        this.message = message;
        // 이후 응답DTO가 사용되는 형태에 따라서 필드 추가 가능
    }


    /**
     * 단순 일기 관련 응답을 뱉을때 사용
     * @param message : 로직 성공시 띄우는 메시지
     * @param diaryId : 생성된 일기 아이디
     * @return : 가공된 응답 DTO 틀을 이용해 반환
     */
    public static DiaryResponse of(String message, Long diaryId) {
        return DiaryResponse.builder()
                .message(message)
                .diaryId(diaryId)
                .build();
    }

}
