package mylog_backend.mylog.diary;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "일기", description = "일기 관련 API")
@RequiredArgsConstructor
@RestController
public class DiaryController {

    private final DiaryService diaryService;

    /**
     * 일기 생성
     * @param request
     * @return
     */
    @Operation(summary = "일기 생성", description = "일기를 생성합니다.")
    @PostMapping("/diaries")
    public ResponseEntity<DiaryResponse> createDiary(@RequestBody DiaryRequest request) {
        DiaryResponse response = diaryService.createDiary(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
