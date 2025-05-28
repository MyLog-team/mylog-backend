package mylog_backend.mylog.memo;

import lombok.RequiredArgsConstructor;
import mylog_backend.mylog.common.exception.UnauthorizedException;
import mylog_backend.mylog.common.exception.UserNotFoundException;
import mylog_backend.mylog.user.User;
import mylog_backend.mylog.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemoService {

    private final MemoRepository memoRepository;
    private final UserRepository userRepository;

    /**
     * 1. 메모 생성 메서드
     * @param memoRequest : 메모 요청 DTO
     * @return : 메모 응답 DTO
     */
    @Transactional
    public MemoResponse createMemo(Long userId, @RequestBody MemoRequest memoRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Memo memo = memoRequest.from();
        user.addMemo(memo); // 연관관계 메서드

        Memo savedMemo =  memoRepository.save(memo);

        return MemoResponse.of("메모가 저장되었습니다.", savedMemo.getId());
    }


    /**
     * 2. 메모 체크시 숨기는 메서드
     */
    @Transactional
    public MemoResponse checkedMemo(Long userId, Long memoId) {
        // 1. 사용자 찾기
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        // 2. 메모 찾기
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("메모를 찾을 수 없습니다."));

        // 3. 메모를 생성한 유저가 맞는지 판별
        if (!memo.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("본인의 메모만 수정할 수 있습니다.");
        }

        user.removeMemo(memo);
        memoRepository.save(memo); // is_checked 업데이트후 DB에 변경내용을 저장 -> 더티체킹 후 변경 내용이 있으면 flush

        return MemoResponse.toCheckedMemo("메모가 체크되어 제거되었습니다.", memo.getId(), memo.getIsChecked());
    }


    /**
     * 3. 메모 목록 조회 메서드
     * - 삭제되지 않은 메모들만 조회
     * @return
     */
    @Transactional(readOnly = true)
    public List<MemoResponse> getVisibleMemos(Long userId) {
        // 1. 사용자 찾기
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        return memoRepository.findByUserIdAndIsVisible(userId, IsVisible.VISIBLE)
                .stream()
                .map(memo -> MemoResponse.toMemo( // <-- toMemo 메서드 사용
                        "메모 목록 조회 성공", // 메시지 (필요에 따라 변경 가능)
                        memo.getId(),
                        memo.getMemoContent(),
                        memo.getIsVisible()))
                .collect(Collectors.toList());
    }


    /**
     * 4. 단일 메모 조회 메서드
     * @param memoId
     * @return
     */
    @Transactional(readOnly = true)
    public MemoResponse getMemo(Long userId, Long memoId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(()-> new IllegalArgumentException("메모를 찾을 수 없습니다."));

        if (memo.getIsVisible() == IsVisible.HIDDEN) {
            throw new IllegalArgumentException("이전에 체크되어 조회할 수 없는 메모입니다.");
        }

        return MemoResponse.builder()
                .message("단일 메모 조회 성공")
                .memoId(memo.getId())
                .isVisible(memo.getIsVisible())
                .memoContent(memo.getMemoContent())
                .isChecked(memo.getIsChecked())
                .build();
    }



}
