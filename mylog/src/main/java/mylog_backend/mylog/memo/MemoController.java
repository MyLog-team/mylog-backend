package mylog_backend.mylog.memo;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    // 메모 생성
    @PostMapping("/memos")
    public ResponseEntity<MemoResponse> createMemo(@RequestBody MemoRequest request) {
        MemoResponse response = memoService.createMemo(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
