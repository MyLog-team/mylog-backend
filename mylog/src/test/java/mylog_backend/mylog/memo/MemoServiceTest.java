package mylog_backend.mylog.memo;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
@SpringBootTest
@ActiveProfiles("test")
public class MemoServiceTest {

    @Autowired
    private MemoService memoService;
    @Autowired
    private MemoRepository memoRepository;


    @Test
    @DisplayName("메모 생성 로직 테스트")
    void createMemo() {
        // given
        MemoRequest memoRequest = MemoRequest.builder()
                .memoContent("음메모~")
                .build();

        // when
        MemoResponse memoResponse = memoService.createMemo(memoRequest);


        // then
        // 메모 응답 DTO에서 아이디 확인
        assertThat(memoResponse.getMemoId()).isNotNull();

        // DB에서 저장된 메모 하나 조회
        Memo savedMemo = memoRepository.findById(memoResponse.getMemoId())
        .orElseThrow(() -> new IllegalArgumentException("메모를 찾을 수 없습니다."));

        // DB에서 아이디로 찾은 메모가 예상대로인지 확인
        assertThat(savedMemo.getMemoContent()).isEqualTo("음메모~");
        assertThat(savedMemo.getIsChecked()).isEqualTo(IsChecked.UNCHECKED);
        assertThat(savedMemo.getIsVisible()).isEqualTo(IsVisible.VISIBLE);

    }


    @Test
    @DisplayName("메모 체크시 논리적 삭제가 이루어져 보이지 않는다.")
    void checkedMemo() {
        // given
        Memo memo = Memo.builder()
                .memoContent("음~메모~")
                .build();

        Memo savedMemo = memoRepository.save(memo);


        // when
        MemoResponse memoResponse = memoService.checkedMemo(savedMemo.getId());


        // then
        Memo updatedMemo = memoRepository.findById(savedMemo.getId())
                .orElseThrow(() -> new IllegalArgumentException("메모를 찾을 수 없습니다."));

        assertThat(updatedMemo.getIsChecked()).isEqualTo(IsChecked.CHECKED);
        assertThat(updatedMemo.getIsVisible()).isEqualTo(IsVisible.HIDDEN);

    }


    @Test
    @DisplayName("메모 목록 조회 기능 - IsVisible.HIDDEN인 데이터는 보이지 않는다.")
    void getVisibleMemos() {
        // given
        Memo visibleMemo1 = Memo.builder()
                .memoContent("음~메모")
                .isVisible(IsVisible.VISIBLE)
                .build();

        Memo visibleMemo2 = Memo.builder()
                .memoContent("음메음메송아지")
                .isVisible(IsVisible.VISIBLE)
                .build();

        Memo hiddenMemo1 = Memo.builder()
                .memoContent("숨겨져야하는 메모")
                .isVisible(IsVisible.HIDDEN)
                .build();

        Memo hiddenMemo2 = Memo.builder()
                .memoContent("비밀내용이지렁")
                .isVisible(IsVisible.HIDDEN)
                .build();


        memoRepository.save(visibleMemo1);
        memoRepository.save(visibleMemo2);

        memoRepository.save(hiddenMemo1);
        memoRepository.save(hiddenMemo2);


        // when
        List<MemoResponse> visibleMemos = memoService.getVisibleMemos();

        // then
        assertThat(visibleMemos).hasSize(2);

    }


    @Test
    @DisplayName("단일 메모 조회 테스트")
    void getMemo() {

        // given
        Memo memo = Memo.builder()
                .memoContent("음~메모~")
                .isVisible(IsVisible.VISIBLE)
                .build();

        Memo savedMemo = memoRepository.save(memo);

        // when
        MemoResponse response = memoService.getMemo(savedMemo.getId());


        // then
        assertThat(response.getMemoId()).isEqualTo(savedMemo.getId());
        assertThat(response.getMemoContent()).isEqualTo("음~메모~");
        assertThat(response.getIsVisible()).isEqualTo(IsVisible.VISIBLE);

    }


    @Test
    @DisplayName("존재하지 않는 메모 조회시 예외가 발생한다.")
    void getNotExistMemo() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> memoService.getMemo(111L));
    }


}
