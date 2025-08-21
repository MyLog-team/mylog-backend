package mylog_backend.mylog.memo;

import mylog_backend.mylog.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemoTest {

    @Test
    @DisplayName("Memo 엔티티 생성 테스트")
    void createMemo() {
        // given
        User user = User.builder()
                .loginId("testUser1")
                .email("test@example.com")
                .password("encoded-password")
                .userName("테스트유저")
                .build();

        // when
        Memo memo = Memo.builder()
                .memoContent("테스트 메모 내용")
                .isChecked(IsChecked.UNCHECKED)
                .isVisible(IsVisible.VISIBLE)
                .user(user)
                .build();

        // then
        assertThat(memo.getMemoContent()).isEqualTo("테스트 메모 내용");
        assertThat(memo.getIsChecked()).isEqualTo(IsChecked.UNCHECKED);
        assertThat(memo.getIsVisible()).isEqualTo(IsVisible.VISIBLE);
        assertThat(memo.getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("메모 체크 기능 테스트")
    void checkMemo() {
        // given
        User user = User.builder()
                .loginId("testUser1")
                .email("test@example.com")
                .password("encoded-password")
                .userName("테스트유저")
                .build();

        Memo memo = Memo.builder()
                .memoContent("체크될 메모")
                .isChecked(IsChecked.UNCHECKED)
                .isVisible(IsVisible.VISIBLE)
                .user(user)
                .build();

        // when
        memo.checkedMemo();

        // then
        assertThat(memo.getIsChecked()).isEqualTo(IsChecked.CHECKED);
        assertThat(memo.getIsVisible()).isEqualTo(IsVisible.HIDDEN);
    }

    @Test
    @DisplayName("IsChecked enum 테스트")
    void testIsCheckedEnum() {
        // given & when & then
        assertThat(IsChecked.CHECKED).isNotNull();
        assertThat(IsChecked.UNCHECKED).isNotNull();
        assertThat(IsChecked.valueOf("CHECKED")).isEqualTo(IsChecked.CHECKED);
        assertThat(IsChecked.values()).hasSize(2);
    }

    @Test
    @DisplayName("IsVisible enum 테스트")
    void testIsVisibleEnum() {
        // given & when & then
        assertThat(IsVisible.VISIBLE).isNotNull();
        assertThat(IsVisible.HIDDEN).isNotNull();
        assertThat(IsVisible.valueOf("VISIBLE")).isEqualTo(IsVisible.VISIBLE);
        assertThat(IsVisible.values()).hasSize(2);
    }

    @Test
    @DisplayName("Memo equals와 hashCode 테스트")
    void testEqualsAndHashCode() {
        // given
        User user = User.builder()
                .loginId("testUser1")
                .email("test@example.com")
                .password("encoded-password")
                .userName("테스트유저")
                .build();

        Memo memo1 = Memo.builder()
                .memoContent("테스트 메모")
                .isChecked(IsChecked.UNCHECKED)
                .isVisible(IsVisible.VISIBLE)
                .user(user)
                .build();

        Memo memo2 = Memo.builder()
                .memoContent("테스트 메모")
                .isChecked(IsChecked.UNCHECKED)
                .isVisible(IsVisible.VISIBLE)
                .user(user)
                .build();

        Memo memo3 = Memo.builder()
                .memoContent("다른 메모")
                .isChecked(IsChecked.CHECKED)
                .isVisible(IsVisible.HIDDEN)
                .user(user)
                .build();

        // then
        assertThat(memo1).isEqualTo(memo2);
        assertThat(memo1).isNotEqualTo(memo3);
        assertThat(memo1.hashCode()).isEqualTo(memo2.hashCode());
    }

    @Test
    @DisplayName("Memo toString 테스트")
    void testToString() {
        // given
        User user = User.builder()
                .loginId("testUser1")
                .email("test@example.com")
                .password("encoded-password")
                .userName("테스트유저")
                .build();

        Memo memo = Memo.builder()
                .memoContent("테스트 메모 내용")
                .isChecked(IsChecked.UNCHECKED)
                .isVisible(IsVisible.VISIBLE)
                .user(user)
                .build();

        // when
        String toString = memo.toString();

        // then
        assertThat(toString).contains("테스트 메모 내용");
        assertThat(toString).contains("UNCHECKED");
        assertThat(toString).contains("VISIBLE");
    }

    @Test
    @DisplayName("기본값 테스트")
    void testDefaultValues() {
        // given
        User user = User.builder()
                .loginId("testUser1")
                .email("test@example.com")
                .password("encoded-password")
                .userName("테스트유저")
                .build();

        // when
        Memo memo = Memo.builder()
                .memoContent("기본값 테스트 메모")
                .user(user)
                .build();

        // then
        assertThat(memo.getIsChecked()).isEqualTo(IsChecked.UNCHECKED);
        assertThat(memo.getIsVisible()).isEqualTo(IsVisible.VISIBLE);
    }
}
