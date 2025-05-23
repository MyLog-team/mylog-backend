package mylog_backend.mylog.diary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "일기 API 요청후 응답으로 나온 결과물을 담는 DTO입니다.")
@Getter
public class DiaryResponse {

    @Schema(description = "성공시 메시지와 함께 결과물이 반환됩니다.", example = "일기 요청 성공")
    private String message;

    @Schema(description = "db에 저장되어있는 일기의 아이디입니다.", example = "123")
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
