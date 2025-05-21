package mylog_backend.mylog.diary;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;

    public DiaryResponse createDiary(DiaryRequest request) {
        Diary diary = request.from();
        Diary savedDiary = diaryRepository.save(diary);

        return DiaryResponse.of("일기가 성공적으로 생성되었습니다.", savedDiary.getId());
    }

}
