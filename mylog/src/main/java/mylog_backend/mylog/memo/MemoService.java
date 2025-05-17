package mylog_backend.mylog.memo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemoService {

    private final MemoRepository memoRepository;

    public MemoResponse createMemo(MemoRequest memoRequest) {
        Memo memo = memoRequest.toMemo();
        Memo savedMemo =  memoRepository.save(memo);

        return new MemoResponse("메모가 저장되었습니다.", savedMemo.getId());
    }



}
