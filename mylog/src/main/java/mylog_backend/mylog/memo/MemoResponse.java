package mylog_backend.mylog.memo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemoResponse {

    private String message;
    private Long memoId;

    @Builder
    public MemoResponse(String message, Long memoId) {
        this.memoId = memoId;
        this.message = message;
    }



    // 메모 생성 응답 DTO의 변환 메서드
    public static MemoResponse of(String message, Long memoId) {
        return MemoResponse.builder()
                .message(message)
                .memoId(memoId)
                .build();
    }
}
