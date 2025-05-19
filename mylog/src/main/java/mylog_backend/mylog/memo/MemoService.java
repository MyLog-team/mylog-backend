package mylog_backend.mylog.memo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemoService {

    private final MemoRepository memoRepository;

    /**
     * 메모 생성 로직
     * @param memoRequest
     * @return
     */
    public MemoResponse createMemo(MemoRequest memoRequest) {
        Memo memo = memoRequest.from();
        Memo savedMemo =  memoRepository.save(memo);

        return MemoResponse.of("메모가 저장되었습니다.", savedMemo.getId());
    }


    /**
     * 메모 체크시 숨기는 로직
     */
    public MemoResponse checkedMemo(Long memoId) {
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("메모를 찾을 수 없습니다."));

        memo.checkedMemo();
        memoRepository.save(memo); // is_checked 업데이트후 DB에 변경내용을 저장 -> 더티체킹 후 변경 내용이 있으면 flush

        return MemoResponse.toCheckedMemo("메모가 체크되어 제거되었습니다.", memo.getId(), memo.getIsChecked());
    }


    /**
     * 메모 목록 조회 메서드
     * - 삭제되지 않은 메모들만 조회
     * @return
     */
    public List<MemoResponse> getVisibleMemos() {
        return memoRepository.findByIsVisible(IsVisible.VISIBLE)
                .stream()
                .map(memo -> MemoResponse.of("메모 조회를 성공하였습니다.", memo.getId()))
                .collect(Collectors.toList());
    }




}
