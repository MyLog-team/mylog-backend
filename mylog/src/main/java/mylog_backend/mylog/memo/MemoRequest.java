package mylog_backend.mylog.memo;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor // memoContent만 있으므로 기본 생성자 이용
@AllArgsConstructor
@Builder
public class MemoRequest {

    @NotNull
    private String memoContent;

    // 메모 생성/수정 일자는 저장시 자동으로 관리

    // isChecked, isVisible는 메모 생성시 기본값 설정

    // MemoRequest 생성자
    // url별로 메모에 대한 매개변수가 필요할때 확장가능
    public MemoRequest(String memoContent, IsChecked isChecked, byte isVisible) {
        this.memoContent = memoContent;
    }

    // 메모 객체 생성 메서드
    // 메모 생성자에서 컨텐트만 가져와서 세팅
    public Memo from() {
        return Memo.builder()
                .memoContent(memoContent)
                .isChecked(IsChecked.UNCHECKED)
                .isVisible(IsVisible.VISIBLE)
                .build();
    }


}
