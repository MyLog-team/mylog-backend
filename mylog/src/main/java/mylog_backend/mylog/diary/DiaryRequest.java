package mylog_backend.mylog.diary;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "일기 요청 DTO")
@Getter
public class DiaryRequest {

    @Schema(description = "일기 제목", example = "123")
    @NotNull
    private String diaryTitle;

    @Schema(description = "일기 내용", example = "엄마저는잘지내요테커사람들이코딩을잘가르쳐줘요...")
    @NotNull
    private String diaryContent;

    @Schema(description = "감정 상태", example = "HAPPY")
    private Feeling feeling;

    @Schema(description = "감정 점수", example = "30")
    // 점수는 0 ~ 100 범위 내에서만 입력되어야함
    @Min(value = 0, message = "감정 점수는 0 이상이어야 합니다")
    @Max(value = 100, message = "감정 점수는 100 이하여야 합니다")
    private Integer feelingScore;

    @Schema(description = "일기 공개 여부", example = "PRIVATE")
    @NotNull
    private IsPublic isPublic;


    /**
     * 일기 요청 DTO 생성자
     * @param diaryTitle : 일기 제목
     * @param diaryContent : 일기 내용
     * @param feeling : 감정
     * @param feelingScore : 감정 점수
     * @param isPublic : 공개 여부
     */
    @Builder
    public DiaryRequest(String diaryTitle, String diaryContent, Feeling feeling, Integer feelingScore, IsPublic isPublic) {
        this.diaryTitle = diaryTitle;
        this.diaryContent = diaryContent;
        this.feeling = feeling;
        this.isPublic = isPublic;
        this.feelingScore = feelingScore;
    }

    /**
     * 일기 생성시 사용되는 변환 메서드
     * 일기 내용 입력후 생성 요청 -> from()메서드로 변환 -> 비즈니스 로직 수행
     * @return : 가공된 일기 엔티티
     */
    public Diary toDiary() {
        return Diary.builder()
                .dairyContent(diaryContent)
                .dairyTitle(diaryTitle)
                .feeling(feeling)
                .feelingScore(feelingScore)
                .isPublic(isPublic)
                .build();
    }



}
