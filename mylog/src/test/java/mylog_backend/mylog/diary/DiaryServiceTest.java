package mylog_backend.mylog.diary;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import mylog_backend.mylog.user.User;
import mylog_backend.mylog.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class DiaryServiceTest {

    @Autowired private DiaryService diaryService;
    @Autowired private DiaryRepository diaryRepository;
    @Autowired private UserRepository userRepository;


    protected User testUser;
    protected DiaryRequest request1;
    protected DiaryRequest request2;
    protected DiaryResponse response1;
    protected DiaryResponse response2;


    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .loginId("testId1234")
                .email("test@example.com")
                .password("encoded-password")
                .userName("테스트유저")
                .build();
        userRepository.save(testUser);

        DiaryRequest request1 = DiaryRequest.builder()
                .diaryTitle("테스트일기1")
                .diaryContent("엄마저는잘지내요테커사람들이코딩을잘가르쳐줘요")
                .feeling(Feeling.SAD)
                .feelingScore(30)
                .isPublic(IsPublic.PRIVATE)
                .build();
        response1 = diaryService.createDiary(testUser.getId(), request1);

        DiaryRequest request2 = DiaryRequest.builder()
                .diaryTitle("테스트일기2")
                .diaryContent("안녕하세요?")
                .feeling(Feeling.HAPPY)
                .feelingScore(50)
                .isPublic(IsPublic.PUBLIC)
                .build();
        response2 = diaryService.createDiary(testUser.getId(), request2);

    }


    // 테스트후 정보 비워줌
    @AfterEach
    void tearDown() {
        userRepository.deleteAll(); // 또는 모든 repository 초기화
    }




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
        DiaryResponse response = diaryService.createDiary(testUser.getId(),request);

        // then
        assertThat(response.getDiaryId()).isNotNull();

        Diary savedDiary = diaryRepository.findById(response.getDiaryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 일기는 존재하지 않습니다."));

        assertThat(savedDiary.getDairyTitle()).isEqualTo("테스트일기");
        assertThat(savedDiary.getDairyContent()).isEqualTo("엄마저는잘지내요테커사람들이코딩을잘가르쳐줘요");
        assertThat(savedDiary.getFeeling()).isEqualTo(Feeling.SAD);
        assertThat(savedDiary.getIsPublic()).isEqualTo(IsPublic.PRIVATE);
        assertThat(savedDiary.getFeelingScore()).isEqualTo(30);

        assertThat(savedDiary.getUser().getId()).isEqualTo(testUser.getId());

    }


    @Test
    @DisplayName("일기 점수는 0부터 100까지만 입력되어야 한다.")
    public void validScore() {

        // given
        DiaryRequest testRequest1 = DiaryRequest.builder()
                .diaryTitle("테스트일기")
                .diaryContent("엄마저는잘지내요테커사람들이코딩을잘가르쳐줘요")
                .feeling(Feeling.SAD)
                .feelingScore(-111)
                .isPublic(IsPublic.PRIVATE)
                .build();

        DiaryRequest testRequest2= DiaryRequest.builder()
                .diaryTitle("테스트일기")
                .diaryContent("엄마저는잘지내요테커사람들이코딩을잘가르쳐줘요")
                .feeling(Feeling.SAD)
                .feelingScore(111)
                .isPublic(IsPublic.PRIVATE)
                .build();


        // when / then
        assertThrows(ConstraintViolationException.class, () -> {
            diaryService.createDiary(testUser.getId() , testRequest1);});
        assertThrows(ConstraintViolationException.class, () -> {
            diaryService.createDiary(testUser.getId(), testRequest2);});

    }


    @Test
    @DisplayName("일기 조회 테스트")
    void getDiary() {
        // given
        Diary savedDiary = diaryRepository.findById(response2.getDiaryId())
                .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다."));

        // when
        DiaryResponse testResponse = diaryService.getDiary(testUser.getId(), savedDiary.getId());

        // then
        assertThat(testResponse.getDiaryId()).isEqualTo(savedDiary.getId());
        assertThat(testResponse.getDairyTitle()).isEqualTo("테스트일기2");
        assertThat(testResponse.getDairyContent()).isEqualTo("안녕하세요?");
        assertThat(testResponse.getIsPublic()).isEqualTo(IsPublic.PUBLIC);
        assertThat(testResponse.getFeeling()).isEqualTo(Feeling.HAPPY);
        assertThat(testResponse.getFeelingScore()).isEqualTo(50);

        assertThat(savedDiary.getUser().getId()).isEqualTo(testUser.getId());

    }




}
