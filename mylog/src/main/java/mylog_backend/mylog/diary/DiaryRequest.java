package mylog_backend.mylog.diary;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import mylog_backend.mylog.memo.Memo;
import org.hibernate.sql.model.jdbc.MergeOperation;


public class DiaryRequest {

    @NotNull
    private String diaryTitle;

    @NotNull
    private String diaryContent;

    private Feeling feeling;

    // 점수는 0 ~ 100 범위 내에서만 입력되어야함
    @Min(value = 0, message = "감정 점수는 0 이상이어야 합니다")
    @Max(value = 100, message = "감정 점수는 100 이하여야 합니다")
    private Integer feelingScore;

    @NotNull
    private IsPublic isPublic;


    /**
     * 일기 요청 DTO 생성자
     * @param diaryTitle
     * @param diaryContent
     * @param feeling
     * @param feelingScore
     * @param isPublic
     */
    @Builder
    public DiaryRequest(String diaryTitle, String diaryContent, Feeling feeling, Integer feelingScore, IsPublic isPublic) {
        this.diaryContent = diaryContent;
        this.diaryTitle = diaryTitle;
        this.feeling = feeling;
        this.isPublic = isPublic;
        this.feelingScore = feelingScore;
    }

    /**
     * 일기 생성시 사용되는 변환 메서드
     * 일기 내용 입력후 생성 요청 -> from()메서드로 변환 -> 비즈니스 로직 수행
     * @return
     */
    public Diary from() {
        return Diary.builder()
                .dairyContent(diaryContent)
                .dairyTitle(diaryTitle)
                .feeling(feeling)
                .feelingScore(feelingScore)
                .isPublic(isPublic)
                .build();
    }



}
