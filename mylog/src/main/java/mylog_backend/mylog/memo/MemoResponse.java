package mylog_backend.mylog.memo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL) // response body에서 null값인 필드는 제외됨
@Schema(description = "메모 관련 요청 처리후 결과물을 담는 응답 DTO입니다.")
@Getter
public class MemoResponse {

    @Schema(description = "API 요청 성공시 결과물과 함께 반환됩니다.", example = "메모 요청 성공")
    private String message;

    @Schema(description = "DB에서 저장된 메모의 id", example = "123")
    private Long memoId;

    @Schema(description = "메모 체크시 상태가 달라집니다.", example = "CHECKED")
    private IsChecked isChecked;
    @Schema(description = "체크 상태가 되면 안보입니다.", example = "HIDDEN")
    private IsVisible isVisible;

    @Schema(description = "db에 저장된 메모 내용입니다.", example = "약국에서 영양제 사기")
    private String memoContent;

    @Builder
    public MemoResponse(String message, Long memoId, IsVisible isVisible, IsChecked isChecked, String memoContent) {
        this.memoId = memoId;
        this.message = message;
        this.isVisible = isVisible;
        this.isChecked = isChecked;
        this.memoContent = memoContent;    }


    /**
     * 메모 관련 응답 메서드
     * 사용하는 곳
     * 1. 메모 생성 성공
     * 2. 메모 조회 성공
     *
     * @param message : 로직 성공시 띄울 메시지
     * @param memoId : 생성된/저장된 메모의 아이디
     * @return : 응답 DTO 틀을 이용하여 값을 할당후 반환
     */
    public static MemoResponse of(String message, Long memoId) {
        return MemoResponse.builder()
                .message(message)
                .memoId(memoId)
                .build();
    }


    /**
     * 메모 체크시 논리적 삭제가 이루어짐
     * @param message : 성공시 응답 메시지
     * @param memoId : 체크된 메모 아이디
     * @param isChecked : CHECKED 수정되어야함
     * @return
     */
    public static MemoResponse toCheckedMemo(String message, Long memoId, IsChecked isChecked) {
        return MemoResponse.builder()
                .message(message)
                .memoId(memoId)
                .isChecked(isChecked)
                .build();
    }


    /**
     * 메모 목록/단건 조회시 이용
     * @param message : 성공시 응답 메시지
     * @param memoId : 조회된 메모 아이디
     * @param memoContent: 조회된 메모의 내용
     * @param isVisible : 조회된 메모가 보이는지 여부
     * @param isChecked : 조회된 메모가 체크되었는지 여부
     * @return : 조회된 메모를 엔티티로 가공하여 반환
     */
    public static MemoResponse toMemo(String message, Long memoId, String memoContent, IsVisible isVisible, IsChecked isChecked) {
        return MemoResponse.builder()
                .memoId(memoId)
                .message(message)
                .memoContent(memoContent)
                .isVisible(isVisible)
                .isChecked(isChecked)
                .build();
    }
}
