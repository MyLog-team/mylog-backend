package mylog_backend.mylog.user;

import mylog_backend.mylog.diary.Diary;
import mylog_backend.mylog.diary.Feeling;
import mylog_backend.mylog.diary.IsPublic;
import mylog_backend.mylog.memo.Memo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    @DisplayName("User 엔티티 생성 테스트")
    void createUser() {
        // given & when
        User user = User.builder()
                .loginId("testUser1")
                .email("test@example.com")
                .password("encoded-password")
                .userName("테스트유저")
                .build();

        // then
        assertThat(user.getLoginId()).isEqualTo("testUser1");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getPassword()).isEqualTo("encoded-password");
        assertThat(user.getUserName()).isEqualTo("테스트유저");
        assertThat(user.getMemos()).isNotNull();
        assertThat(user.getDiaries()).isNotNull();
    }

    @Test
    @DisplayName("User에 메모 추가 테스트")
    void addMemoToUser() {
        // given
        User user = User.builder()
                .loginId("testUser1")
                .email("test@example.com")
                .password("encoded-password")
                .userName("테스트유저")
                .build();

        Memo memo = Memo.builder()
                .memoContent("테스트 메모")
                .user(user)
                .build();

        // when
        user.getMemos().add(memo);

        // then
        assertThat(user.getMemos()).hasSize(1);
        assertThat(user.getMemos().get(0).getMemoContent()).isEqualTo("테스트 메모");
    }

    @Test
    @DisplayName("User에 일기 추가 테스트")
    void addDiaryToUser() {
        // given
        User user = User.builder()
                .loginId("testUser1")
                .email("test@example.com")
                .password("encoded-password")
                .userName("테스트유저")
                .build();

        Diary diary = Diary.builder()
                .dairyTitle("테스트 일기")
                .dairyContent("테스트 내용")
                .feeling(Feeling.HAPPY)
                .feelingScore(80)
                .isPublic(IsPublic.PRIVATE)
                .user(user)
                .build();

        // when
        user.getDiaries().add(diary);

        // then
        assertThat(user.getDiaries()).hasSize(1);
        assertThat(user.getDiaries().get(0).getDairyTitle()).isEqualTo("테스트 일기");
    }

    @Test
    @DisplayName("User equals와 hashCode 테스트")
    void testEqualsAndHashCode() {
        // given
        User user1 = User.builder()
                .loginId("testUser1")
                .email("test@example.com")
                .password("encoded-password")
                .userName("테스트유저")
                .build();

        User user2 = User.builder()
                .loginId("testUser1")
                .email("test@example.com")
                .password("encoded-password")
                .userName("테스트유저")
                .build();

        User user3 = User.builder()
                .loginId("differentUser")
                .email("different@example.com")
                .password("different-password")
                .userName("다른유저")
                .build();

        // then
        assertThat(user1).isEqualTo(user2);
        assertThat(user1).isNotEqualTo(user3);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }

    @Test
    @DisplayName("User toString 테스트")
    void testToString() {
        // given
        User user = User.builder()
                .loginId("testUser1")
                .email("test@example.com")
                .password("encoded-password")
                .userName("테스트유저")
                .build();

        // when
        String toString = user.toString();

        // then
        assertThat(toString).contains("testUser1");
        assertThat(toString).contains("test@example.com");
        assertThat(toString).contains("테스트유저");
    }
}
