package mylog_backend.mylog.memo;

import lombok.RequiredArgsConstructor;
import mylog_backend.mylog.common.exception.UnauthorizedException;
import mylog_backend.mylog.common.exception.UserNotFoundException;
import mylog_backend.mylog.user.User;
import mylog_backend.mylog.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * @return : of()로 성공시 메시지와 저장된 메모 아이디를 반환
     */
    @Transactional
    public MemoResponse createMemo(Long userId, MemoRequest memoRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        Memo memo = memoRequest.toMemo();
        user.addMemo(memo); // 연관관계 메서드

        Memo savedMemo =  memoRepository.save(memo);

        return MemoResponse.of("메모가 저장되었습니다.", savedMemo.getId());
    }


    /**
     * 메모를 체크시 논리적 삭제가 이루어져 보이지 않음
     * @param userId : 요청한 사용자의 아이디
     * @param memoId : 체크 표시가 요청된 메모의 아이디
     * @return : toCheckedMemo()로 성공 메시지와 체크 표시된 메모 아이디, 체크 여부를 반환
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
            throw new UnauthorizedException("본인의 메모만 체크할 수 있습니다.");
        }

        // 4. user에서 정의한 연관관계 메서드로 메모를 논리적 삭제
        user.removeMemo(memo);
        // 5.변경된 메모 저장
//        memoRepository.save(memo); // is_checked 업데이트후 DB에 변경내용을 저장 -> 더티체킹 후 변경 내용이 있으면 flush

        return MemoResponse.toCheckedMemo("메모가 체크되어 제거되었습니다.", memo.getId(), memo.getIsChecked());
    }


    /**
     * 3. 메모 목록 조회 메서드
     *  논리적 삭제가 되지 않은 메모들만 조회
     * @param userId : 요청한 사용자 아이디
     * @return : VISIBLE인 메모만 골라서 반환
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
                        memo.getIsVisible(),
                        memo.getIsChecked()))
                .toList();
    }


    /**
     * 4. 단일 메모 조회 메서드
     * @param memoId : 사용자가 요청한 하나의 메모 아이디
     * @param userId : 요청한 사용자 아이디
     * @return :
     */
    @Transactional(readOnly = true)
    public MemoResponse getMemo(Long userId, Long memoId) {
        // 1. 유효한 사용자인지 판단
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // 2. 메모를 조회하여 할당
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(()-> new IllegalArgumentException("메모를 찾을 수 없습니다."));

        // 3. IsVisible이 HIDDEN이면 조회할 수 없음
        if (memo.getIsVisible() == IsVisible.HIDDEN) {
            throw new IllegalArgumentException("이전에 체크되어 조회할 수 없는 메모입니다.");
        }

        // 4. 본인이 작성한 메모만 조회 가능
        if (!memo.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("본인의 메모만 수정할 수 있습니다.");
        }

        // 5. 모든 로직을 거쳤다면, 조회된 메모를 엔티티로 가공하여 반환
        return MemoResponse.toMemo( // <-- toMemo 메서드 사용
                "단일 메모 조회 성공", // 메시지 (필요에 따라 변경 가능)
                memo.getId(),
                memo.getMemoContent(),
                memo.getIsVisible(),
                memo.getIsChecked()
        );
    }



}
