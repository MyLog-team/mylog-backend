package mylog_backend.mylog.diary;

import mylog_backend.mylog.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DiaryTest {

    @Test
    @DisplayName("Diary 엔티티 생성 테스트")
    void createDiary() {
        // given
        User user = User.builder()
                .loginId("testUser1")
                .email("test@example.com")
                .password("encoded-password")
                .userName("테스트유저")
                .build();

        // when
        Diary diary = Diary.builder()
                .dairyTitle("테스트 일기")
                .dairyContent("오늘은 좋은 하루였다")
                .feeling(Feeling.HAPPY)
                .feelingScore(80)
                .isPublic(IsPublic.PRIVATE)
                .user(user)
                .build();

        // then
        assertThat(diary.getDairyTitle()).isEqualTo("테스트 일기");
        assertThat(diary.getDairyContent()).isEqualTo("오늘은 좋은 하루였다");
        assertThat(diary.getFeeling()).isEqualTo(Feeling.HAPPY);
        assertThat(diary.getFeelingScore()).isEqualTo(80);
        assertThat(diary.getIsPublic()).isEqualTo(IsPublic.PRIVATE);
        assertThat(diary.getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("Diary 빌더 패턴 테스트")
    void createDiaryWithBuilder() {
        // given
        User user = User.builder()
                .loginId("testUser1")
                .email("test@example.com")
                .password("encoded-password")
                .userName("테스트유저")
                .build();

        // when - 다른 값들로 새로운 Diary 생성
        Diary originalDiary = Diary.builder()
                .dairyTitle("원래 제목")
                .dairyContent("원래 내용")
                .feeling(Feeling.SAD)
                .feelingScore(30)
                .isPublic(IsPublic.PRIVATE)
                .user(user)
                .build();

        Diary updatedDiary = Diary.builder()
                .dairyTitle("수정된 제목")
                .dairyContent("수정된 내용")
                .feeling(Feeling.HAPPY)
                .feelingScore(90)
                .isPublic(IsPublic.PUBLIC)
                .user(user)
                .build();

        // then - 원래 일기와 수정된 일기가 다른 값을 가지는지 확인
        assertThat(originalDiary.getDairyTitle()).isEqualTo("원래 제목");
        assertThat(updatedDiary.getDairyTitle()).isEqualTo("수정된 제목");
        assertThat(updatedDiary.getDairyContent()).isEqualTo("수정된 내용");
        assertThat(updatedDiary.getFeeling()).isEqualTo(Feeling.HAPPY);
        assertThat(updatedDiary.getFeelingScore()).isEqualTo(90);
        assertThat(updatedDiary.getIsPublic()).isEqualTo(IsPublic.PUBLIC);
    }

    @Test
    @DisplayName("Feeling enum 테스트")
    void testFeelingEnum() {
        // given & when & then
        assertThat(Feeling.HAPPY).isNotNull();
        assertThat(Feeling.SAD).isNotNull();
        assertThat(Feeling.NOT_BAD).isNotNull();
        assertThat(Feeling.ANGRY).isNotNull();
        assertThat(Feeling.valueOf("HAPPY")).isEqualTo(Feeling.HAPPY);
        assertThat(Feeling.values()).hasSize(4);
    }

    @Test
    @DisplayName("IsPublic enum 테스트")
    void testIsPublicEnum() {
        // given & when & then
        assertThat(IsPublic.PUBLIC).isNotNull();
        assertThat(IsPublic.PRIVATE).isNotNull();
        assertThat(IsPublic.valueOf("PUBLIC")).isEqualTo(IsPublic.PUBLIC);
        assertThat(IsPublic.values()).hasSize(2);
    }

    @Test
    @DisplayName("Diary equals와 hashCode 테스트")
    void testEqualsAndHashCode() {
        // given
        User user = User.builder()
                .loginId("testUser1")
                .email("test@example.com")
                .password("encoded-password")
                .userName("테스트유저")
                .build();

        Diary diary1 = Diary.builder()
                .dairyTitle("테스트 일기")
                .dairyContent("내용")
                .feeling(Feeling.HAPPY)
                .feelingScore(80)
                .isPublic(IsPublic.PRIVATE)
                .user(user)
                .build();

        Diary diary2 = Diary.builder()
                .dairyTitle("테스트 일기")
                .dairyContent("내용")
                .feeling(Feeling.HAPPY)
                .feelingScore(80)
                .isPublic(IsPublic.PRIVATE)
                .user(user)
                .build();

        Diary diary3 = Diary.builder()
                .dairyTitle("다른 일기")
                .dairyContent("다른 내용")
                .feeling(Feeling.SAD)
                .feelingScore(30)
                .isPublic(IsPublic.PUBLIC)
                .user(user)
                .build();

        // then
        assertThat(diary1).isEqualTo(diary2);
        assertThat(diary1).isNotEqualTo(diary3);
        assertThat(diary1.hashCode()).isEqualTo(diary2.hashCode());
    }

    @Test
    @DisplayName("Diary toString 테스트")
    void testToString() {
        // given
        User user = User.builder()
                .loginId("testUser1")
                .email("test@example.com")
                .password("encoded-password")
                .userName("테스트유저")
                .build();

        Diary diary = Diary.builder()
                .dairyTitle("테스트 일기")
                .dairyContent("오늘은 좋은 하루였다")
                .feeling(Feeling.HAPPY)
                .feelingScore(80)
                .isPublic(IsPublic.PRIVATE)
                .user(user)
                .build();

        // when
        String toString = diary.toString();

        // then
        assertThat(toString).contains("테스트 일기");
        assertThat(toString).contains("HAPPY");
        assertThat(toString).contains("80");
    }
}
