package mylog_backend.mylog.memo;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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


}
