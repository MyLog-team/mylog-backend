package mylog_backend.mylog.memo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mylog_backend.mylog.auth.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "메모", description = "메모 관련 API")
@RequiredArgsConstructor
@RestController // API 요청을 처리하기 위한 컨트롤러
public class MemoController {

    private final MemoService memoService;

    /**
     * 메모 생성 메서드
     * @param request : 메모 요청 DTO
     * @param userPrincipal : 인증된 사용자를 담는 객체
     * @return : 성공적으로 생성시 응답과 함께 201 상태코드 반환
     */
    @Operation(summary = "메모 생성", description = "새 메모를 생성합니다.")
    @PostMapping("/memos")
    public ResponseEntity<MemoResponse> createMemo(@RequestBody MemoRequest request, @AuthenticationPrincipal UserPrincipal userPrincipal) {

        // 1. 사용자의 아이디를 가져옴
        Long userId = userPrincipal.getId();

        // 2. 메모 서비스에서 메모 생성 메서드의 결과물을 response를 할당
        MemoResponse response = memoService.createMemo(userId, request); // 사용자 id가 추가됨
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    /**
     * 메모 체크시 논리적 삭제가 이루어지는 기능
     * @param memoId : 체크표시 요청된 메모
     * @param userPrincipal : 인증된 사용자를 담는 객체
     * @return : 성공시 응답과 함께 200 상태 코드 반환
     */
    @Operation(summary = "메모 확인 표시", description = "메모를 체크했을때 논리적 삭제가 이루어집니다.")
    @PatchMapping("/memos/{memoId}")
    public ResponseEntity<MemoResponse> checkedMemo(@PathVariable Long memoId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        // 1. userPrincipal에서 사용자 아이디를 할당
        Long userId = userPrincipal.getId();

        // 2. 서비스의 메서드를 이용해 메모 체크 로직 수행
        MemoResponse response = memoService.checkedMemo(userId, memoId);
        return ResponseEntity.ok(response);
    }

    /**
     * 메모 목록 조회 기능
     * @param userPrincipal : 인증된 사용자를 담는 객체
     */
    @Operation(summary = "메모 목록 조회", description = "체크되지 않은 메모들을 불러옵니다.")
    @GetMapping("/memos")
    public ResponseEntity<List<MemoResponse>> getVisibleMemos(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getId();

        List<MemoResponse> memos = memoService.getVisibleMemos(userId);
        return ResponseEntity.ok(memos);
    }

    /**
     * 단일 메모 조회 기능
     * @param memoId : 체크표시 요청된 메모
     * @param userPrincipal : 인증된 사용자를 담는 객체
     */
    @Operation(summary = "단일 메모 조회", description = "메모를 하나씩 조회합니다.")
    @GetMapping("/memos/{memoId}")
    public ResponseEntity<MemoResponse> getMemo(@PathVariable Long memoId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getId();

        MemoResponse response = memoService.getMemo(userId, memoId);
        return ResponseEntity.ok(response);
    }

}
