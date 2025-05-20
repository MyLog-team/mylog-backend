package mylog_backend.mylog.memo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemoResponse {

    private String message;
    private Long memoId;

    private IsChecked isChecked;
    private IsVisible isVisible;

    @Builder
    public MemoResponse(String message, Long memoId, IsVisible isVisible, IsChecked isChecked) {
        this.memoId = memoId;
        this.message = message;
        this.isVisible = isVisible;
        this.isChecked = isChecked;
    }


    /**
     * 메모 관련 응답 메서드
     * 사용하는 곳
     * 1. 메모 생성 성공
     * 2. 메모 조회 성공
     * 3. 메모 목록 조회 성공
     *
     * @param message
     * @param memoId
     * @return
     */
    public static MemoResponse of(String message, Long memoId) {
        return MemoResponse.builder()
                .message(message)
                .memoId(memoId)
                .build();
    }


    // 메모 체크시 제거 응답 DTO 메서드
    public static MemoResponse toCheckedMemo(String message, Long memoId, IsChecked isChecked) {
        return MemoResponse.builder()
                .message(message)
                .memoId(memoId)
                .isChecked(isChecked)
                .build();
    }
}
