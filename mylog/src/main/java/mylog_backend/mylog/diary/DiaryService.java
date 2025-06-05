package mylog_backend.mylog.diary;

import lombok.RequiredArgsConstructor;
import mylog_backend.mylog.common.exception.UnauthorizedException;
import mylog_backend.mylog.common.exception.UserNotFoundException;
import mylog_backend.mylog.user.User;
import mylog_backend.mylog.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    /**
     * 1. 일기 생성
     * @param userId : 유저 아이디
     * @param request : 프론트엔드에게 일기에 대한 내용을 입력받음
     * @return : 응답 성공 메시지, 저장된 일기 아이디 반환
     */
    @Transactional // 해당 기능은 한 트랜잭션에서 작용
    public DiaryResponse createDiary(Long userId , DiaryRequest request) { // userId로 사용자의 일기를 구분
        // 사용자를 Id로 찾아옴
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        // 요청 DTO로 입력된 내용을 일기 엔티티로 가공
        Diary diary = request.toDiary();

        // 일기 레포지토리에 저장 전 사용자와 관계를 맺어줌
        user.addDiary(diary);

        // 일기를 레포지토리에 저장, 저장된 일기를 할당받음
        Diary savedDiary = diaryRepository.save(diary);

        // 성공 메시지와 함께 저장된 일기 아이디를 반환
        return DiaryResponse.of("일기가 성공적으로 생성되었습니다.", savedDiary.getId());
    }


    /**
     * 2. 일기 목록 조회 메서드
     * @param userId : 사용자 아이디
     * @return : 사용자 아이디로 가져온 모든 일기들을 stream 객체로 가공해 반환
     */
    @Transactional(readOnly = true)
    public List<DiaryResponse> getDiaries(Long userId) {
        // 인증된 사용자인지 판단
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // 해당 유저가 작성한 일기를 모두 조회
        List<Diary> diaries = diaryRepository.findAllByUserId(userId);

        // 일기 필터링
        List<Diary> filteredDiaries = diaries.stream()
                .filter(diary -> {
                    Long diaryWriterId = diary.getUser().getId(); // 작성자 아이디를 추출
                    if (userId.equals(diaryWriterId)) { // 작성자 아이디와 userId가 맞다면 true
                        return true;
                    }
                    else { // 작성자와 userId가 다르다면 PUBLIC으로 공개된 일기만 가져옴
                        return diary.getIsPublic() == IsPublic.PUBLIC;
                    }
                })
                .toList();

        return filteredDiaries
                .stream() // 조회된 일기들을 stream 객체로 가공
                .map(diary ->  DiaryResponse.toDiary( // 조회된 diary들을 응답 DTO의 toDiary 형태에 맞게 매핑
                        "일기 목록 조회 성공",
                        diary.getId(),
                        diary.getDairyTitle(),
                        diary.getDairyContent(),
                        diary.getFeeling(),
                        diary.getIsPublic(),
                        diary.getFeelingScore()))
                .toList(); // 매핑된 DTO들을 다시 List로 가공
    }


    /**
     * 3. 일기 단건 조회 메서드
     * @param userId : 요청한 사용자 아이디
     * @param diaryId : 조회하려는 일기 아이디
     * @return : 일기 단건을 반환
     */
    @Transactional(readOnly = true)
    public DiaryResponse getDiary(Long userId, Long diaryId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(()-> new IllegalArgumentException("일기를 찾을 수 없습니다."));

        // 일기 작성자와 맞는지 판단하는 로직
        if (diary.getIsPublic() == IsPublic.PRIVATE) { // 공개여부가 PRIVATE 일때 작성자만 조회 가능
            if (!diary.getUser().getId().equals(userId)) { // 일기 작성자의 아이디가 userId와 일치하지 않을때 예외 발생
                throw new UnauthorizedException("해당 일기를 조회할 권한이 없습니다.");
            }
        }

        return DiaryResponse.toDiary(
                "단일 일기 조회 성공",
                diary.getId(),
                diary.getDairyTitle(),
                diary.getDairyContent(),
                diary.getFeeling(),
                diary.getIsPublic(),
                diary.getFeelingScore());

    }

}
