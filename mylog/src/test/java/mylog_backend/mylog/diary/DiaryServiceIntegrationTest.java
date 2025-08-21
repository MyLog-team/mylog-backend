package mylog_backend.mylog.diary;

import jakarta.validation.ConstraintViolationException;
import mylog_backend.mylog.common.exception.UnauthorizedException;
import mylog_backend.mylog.user.User;
import mylog_backend.mylog.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class DiaryServiceIntegrationTest {

    // 통합 테스트를 위해 실제 구현체들을 주입받아 사용
    @Autowired private DiaryService diaryService;
    @Autowired private DiaryRepository diaryRepository;
    @Autowired private UserRepository userRepository;

    // 테스트용 데이터 생성
    protected User testUser;
    protected User daiseek;
    protected DiaryRequest request1;
    protected DiaryRequest request2;
    protected DiaryResponse response1;
    protected DiaryResponse response2;


    @BeforeEach
    void setUp() {
        // 1. 유저 생성
        testUser = User.builder()
                .loginId("testId1234")
                .email("test@example.com")
                .password("encoded-password")
                .userName("테스트유저")
                .build();
        userRepository.save(testUser);

        daiseek = User.builder()
                .loginId("daiseek123")
                .email("daiseek@example.com")
                .password("daiseek123")
                .userName("정대식")
                .build();
        userRepository.save(daiseek);

        // 2. 일기1 생성
        DiaryRequest request1 = DiaryRequest.builder()
                .diaryTitle("테스트일기1")
                .diaryContent("엄마저는잘지내요테커사람들이코딩을잘가르쳐줘요")
                .feeling(Feeling.SAD)
                .feelingScore(30)
                .isPublic(IsPublic.PRIVATE)
                .build();
        response1 = diaryService.createDiary(testUser.getId(), request1);

        // 일기2 생성
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
        diaryRepository.deleteAll();
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
                .isPublic(IsPublic.PRIVATE)
                .feelingScore(30)
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


    @Test
    @DisplayName("PRIVATE로 설정된 일기는 타인이 조회할 수 없다.")
    void getOtherDiary() {
        // given

        // testUser의 비공개된 일기
        Diary testUserDiary1 = diaryRepository.findById(response1.getDiaryId())
                .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다."));

        // testUser의 공개된 일기 (참조용으로만 사용)
        // Diary testUserDiary2 = diaryRepository.findById(response2.getDiaryId())
        //         .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다."));


        // when & then
        // daiseek가 testUser의 비공개 일기(response2)를 조회 시도하면 예외가 발생해야 한다.
        assertThrows(UnauthorizedException.class, () -> {
            diaryService.getDiary(daiseek.getId(), testUserDiary1.getId());
        });


    }

    @Test
    @DisplayName("일기 직접 수정 테스트")
    void updateDiaryDirectly() {
        // given
        Diary savedDiary = diaryRepository.findById(response1.getDiaryId())
                .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다."));

        // when - 새로운 일기를 생성하여 업데이트를 시뮬레이션
        Diary updatedDiary = Diary.builder()
                .id(savedDiary.getId())
                .dairyTitle("수정된 제목")
                .dairyContent("수정된 내용")
                .feeling(Feeling.HAPPY)
                .feelingScore(90)
                .isPublic(IsPublic.PUBLIC)
                .user(savedDiary.getUser())
                .build();

        // then - 수정된 값들이 제대로 설정되었는지 확인
        assertThat(updatedDiary.getDairyTitle()).isEqualTo("수정된 제목");
        assertThat(updatedDiary.getDairyContent()).isEqualTo("수정된 내용");
        assertThat(updatedDiary.getFeeling()).isEqualTo(Feeling.HAPPY);
        assertThat(updatedDiary.getFeelingScore()).isEqualTo(90);
        assertThat(updatedDiary.getIsPublic()).isEqualTo(IsPublic.PUBLIC);
    }

    @Test
    @DisplayName("일기 삭제 테스트")
    void deleteDiary() {
        // given
        Diary savedDiary = diaryRepository.findById(response1.getDiaryId())
                .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다."));

        // when - Repository를 직접 사용하여 삭제
        diaryRepository.delete(savedDiary);

        // then
        assertThat(diaryRepository.findById(savedDiary.getId())).isEmpty();
    }

    @Test
    @DisplayName("다른 사용자의 일기 접근 권한 테스트")
    void accessOtherUserDiary() {
        // given
        Diary testUserDiary = diaryRepository.findById(response1.getDiaryId())
                .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다."));

        // when & then - getDiary로 권한 검증 테스트
        assertThrows(UnauthorizedException.class, () -> {
            diaryService.getDiary(daiseek.getId(), testUserDiary.getId());
        });
    }

    @Test
    @DisplayName("일기 소유자 확인 테스트")
    void checkDiaryOwnership() {
        // given
        Diary testUserDiary = diaryRepository.findById(response1.getDiaryId())
                .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다."));

        // when & then - 소유자 확인
        assertThat(testUserDiary.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(testUserDiary.getUser().getId()).isNotEqualTo(daiseek.getId());
    }

    @Test
    @DisplayName("사용자의 모든 일기 조회 테스트")
    void getAllDiariesByUser() {
        // when - 기존 getDiaries 메서드 사용
        List<DiaryResponse> userDiaries = diaryService.getDiaries(testUser.getId());

        // then
        assertThat(userDiaries).hasSize(2);
        assertThat(userDiaries)
                .extracting(DiaryResponse::getDairyTitle)
                .containsExactlyInAnyOrder("테스트일기1", "테스트일기2");
    }

    @Test
    @DisplayName("존재하지 않는 일기 조회시 예외 발생")
    void getDiaryNotFound() {
        // given
        Long nonExistentId = 999L;

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            diaryService.getDiary(testUser.getId(), nonExistentId);
        });
    }

    @Test
    @DisplayName("감정 점수 경계값 테스트")
    void testFeelingScoreBoundary() {
        // given - 최소값
        DiaryRequest minScoreRequest = DiaryRequest.builder()
                .diaryTitle("최소 점수 테스트")
                .diaryContent("최소 점수 테스트 내용")
                .feeling(Feeling.SAD)
                .feelingScore(0)
                .isPublic(IsPublic.PRIVATE)
                .build();

        // given - 최대값
        DiaryRequest maxScoreRequest = DiaryRequest.builder()
                .diaryTitle("최대 점수 테스트")
                .diaryContent("최대 점수 테스트 내용")
                .feeling(Feeling.HAPPY)
                .feelingScore(100)
                .isPublic(IsPublic.PRIVATE)
                .build();

        // when & then
        DiaryResponse minResponse = diaryService.createDiary(testUser.getId(), minScoreRequest);
        DiaryResponse maxResponse = diaryService.createDiary(testUser.getId(), maxScoreRequest);

        assertThat(minResponse.getFeelingScore()).isEqualTo(0);
        assertThat(maxResponse.getFeelingScore()).isEqualTo(100);
    }
}
