package mylog_backend.mylog.diary;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class DiaryController {

    private final DiaryService diaryService;

    /**
     * 일기 생성
     * @param request
     * @return
     */
    @PostMapping("/diaries")
    public ResponseEntity<DiaryResponse> createDiary(@RequestBody DiaryRequest request) {
        DiaryResponse response = diaryService.createDiary(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
