package mylog_backend.mylog.diary;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class DiaryServiceTest {

    @Autowired private DiaryService diaryService;
    @Autowired private DiaryRepository diaryRepository;


    @Test
    @DisplayName("일기 생성 로직 테스트")
    void createDiary() {
        // given
        DiaryRequest request = DiaryRequest.builder()
                .diaryTitle("테스트일기")
                .diaryContent("엄마저는잘지내요테커사람들이코딩을잘가르쳐줘요")
                .feeling(Feeling.SAD)
                .feelingScore(30)
                .isPublic(IsPublic.PRIVATE)
                .build();

        // when
        DiaryResponse response = diaryService.createDiary(request);

        // then
        assertThat(response.getDiaryId()).isNotNull();

        Diary savedDiary = diaryRepository.findById(response.getDiaryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 일기는 존재하지 않습니다."));

        assertThat(savedDiary.getDairyTitle()).isEqualTo("테스트일기");
        assertThat(savedDiary.getDairyContent()).isEqualTo("엄마저는잘지내요테커사람들이코딩을잘가르쳐줘요");
        assertThat(savedDiary.getFeeling()).isEqualTo(Feeling.SAD);
        assertThat(savedDiary.getIsPublic()).isEqualTo(IsPublic.PRIVATE);
        assertThat(savedDiary.getFeelingScore()).isEqualTo(30);

    }


    @Test
    @DisplayName("일기 점수는 0부터 100까지만 입력되어야 한다.")
    public void validScore() {

        // given
        DiaryRequest request1 = DiaryRequest.builder()
                .diaryTitle("테스트일기")
                .diaryContent("엄마저는잘지내요테커사람들이코딩을잘가르쳐줘요")
                .feeling(Feeling.SAD)
                .feelingScore(-111)
                .isPublic(IsPublic.PRIVATE)
                .build();

        DiaryRequest request2= DiaryRequest.builder()
                .diaryTitle("테스트일기")
                .diaryContent("엄마저는잘지내요테커사람들이코딩을잘가르쳐줘요")
                .feeling(Feeling.SAD)
                .feelingScore(111)
                .isPublic(IsPublic.PRIVATE)
                .build();


        // when / then
        assertThrows(ConstraintViolationException.class, () -> {
            diaryService.createDiary(request1);});
        assertThrows(ConstraintViolationException.class, () -> {
            diaryService.createDiary(request2);});



    }




}
