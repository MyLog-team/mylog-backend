package mylog_backend.mylog.diary;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import mylog_backend.mylog.memo.MemoResponse;

@JsonInclude(JsonInclude.Include.NON_NULL) // response body에서 null값인 필드는 제외됨
@Schema(description = "일기 API 요청후 응답으로 나온 결과물을 담는 DTO입니다.")
@Getter
public class DiaryResponse {

    @Schema(description = "성공시 메시지와 함께 결과물이 반환됩니다.", example = "일기 요청 성공")
    private String message;

    @Schema(description = "db에 저장되어있는 일기의 아이디입니다.", example = "123")
    private Long diaryId;

    @Schema(description = "일기 내용")
    private String dairyContent;

    @Schema(description = "일기 제목")
    private String dairyTitle;

    @Schema(description = "공개 여부")
    private IsPublic isPublic;

    @Schema(description = "감정")
    private Feeling feeling;

    @Schema(description = "감정 점수")
    private Integer feelingScore;



    /**
     * 일기 응답 DTO 생성자
     * 일기 관련 로직후 이용됨
     * 로직이 다양해질수록 확장성을 띄도록 설계
     * @param message : 일기 요청 성공시 메시지
     * @param diaryId : 일기 아이디
     */
    @Builder
    public DiaryResponse(String message, Long diaryId, String dairyTitle,
                         String dairyContent, Feeling feeling,
                         IsPublic isPublic, Integer feelingScore) {
        this.diaryId = diaryId;
        this.message = message;
        this.dairyContent = dairyContent;
        this.dairyTitle = dairyTitle;
        this.isPublic = isPublic;
        this.feeling = feeling;
        this.feelingScore = feelingScore;
    }


    /**
     * 일기 단건/목록 조회시 사용
     * @param message : 일기 조회 요청 성공시 메시지
     * @param diaryId : 일기 아이디
     * @param dairyTitle : 일기 제목
     * @param dairyContent : 일기 내용
     * @param feeling : 감정
     * @param isPublic : 공개여부
     * @param feelingScore : 감정점수
     * @return : 일기 응답 DTO로 가공된 데이터
     */
    public static DiaryResponse toDiary(String message, Long diaryId, String dairyTitle,
                                        String dairyContent, Feeling feeling,
                                        IsPublic isPublic, Integer feelingScore) {
        return DiaryResponse.builder()
                .message(message)
                .diaryId(diaryId)
                .dairyTitle(dairyTitle)
                .dairyContent(dairyContent)
                .isPublic(isPublic)
                .feeling(feeling)
                .feelingScore(feelingScore)
                .build();
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
