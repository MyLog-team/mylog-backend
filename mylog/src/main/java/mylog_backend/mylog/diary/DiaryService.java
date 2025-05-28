package mylog_backend.mylog.diary;

import lombok.RequiredArgsConstructor;
import mylog_backend.mylog.common.exception.UserNotFoundException;
import mylog_backend.mylog.memo.MemoResponse;
import mylog_backend.mylog.user.User;
import mylog_backend.mylog.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    /**
     * 1. 일기 생성 메서드
     * @param userId : 유저 아이디
     * @param request : 프론트에게 일기에 대한 내용을 입력받음
     * @return : 응답을 가공하여 반환
     */
    @Transactional
    public DiaryResponse createDiary(Long userId , @RequestBody DiaryRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        Diary diary = request.from();

        user.addDiary(diary);

        Diary savedDiary = diaryRepository.save(diary);

        return DiaryResponse.of("일기가 성공적으로 생성되었습니다.", savedDiary.getId());
    }


    /**
     * 2. 일기 목록 조회 메서드
     * @param userId
     * @return
     */
    @Transactional
    public List<DiaryResponse> getDiaries(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        return diaryRepository.findAllByUserId(userId)
                .stream()
                .map(diary -> DiaryResponse.toDiary(
                        "일기 목록 조회 성공",
                        diary.getId(),
                        diary.getDairyTitle(),
                        diary.getDairyContent(),
                        diary.getFeeling(),
                        diary.getIsPublic(),
                        diary.getFeelingScore()))
                .collect(Collectors.toList());
    }


    /**
     * 3. 일기 단건 조회 메서드
     * @param userId
     * @param diaryId
     * @return
     */
    @Transactional
    public DiaryResponse getDiary(Long userId, Long diaryId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(()-> new IllegalArgumentException("메모를 찾을 수 없습니다."));

        return DiaryResponse.builder()
                .message("단일 일기 조회 성공")
                .diaryId(diary.getId())
                .dairyTitle(diary.getDairyTitle())
                .dairyContent(diary.getDairyContent())
                .feeling(diary.getFeeling())
                .feelingScore(diary.getFeelingScore())
                .isPublic(diary.getIsPublic())
                .build();

    }

}
