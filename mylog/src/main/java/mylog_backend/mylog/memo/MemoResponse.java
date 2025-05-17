package mylog_backend.mylog.memo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemoResponse {

    private String message;
    private Long memoId;
}
