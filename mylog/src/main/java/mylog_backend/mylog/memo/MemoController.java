package mylog_backend.mylog.memo;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController // API 요청을 처리하기 위한 컨트롤러
public class MemoController {

    private final MemoService memoService;

    /**
     * 메모 생성
     * @param request
     * @return
     */
    @PostMapping("/memos")
    public ResponseEntity<MemoResponse> createMemo(@RequestBody MemoRequest request) {
        MemoResponse response = memoService.createMemo(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    /**
     * 메모 제거 기능
     */
    @PatchMapping("/memos/{memoId}")
    public ResponseEntity<MemoResponse> checkedMemo(@PathVariable Long memoId) {
        MemoResponse response = memoService.checkedMemo(memoId);
        return ResponseEntity.ok(response);
    }

    /**
     * 메모 목록 조회 기능
     */
    @GetMapping("/memos")
    public ResponseEntity<List<MemoResponse>> getVisibleMemos() {
        List<MemoResponse> memos = memoService.getVisibleMemos();
        return ResponseEntity.ok(memos);
    }

    /**
     * 단일 메모 조회 기능
     */
    @GetMapping("/memos/{memoId}")
    public ResponseEntity<MemoResponse> getMemo(@PathVariable Long memoId) {
        MemoResponse response = memoService.getMemo(memoId);
        return ResponseEntity.ok(response);
    }

}
