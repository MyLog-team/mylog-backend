package mylog_backend.mylog.diary;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mylog_backend.mylog.auth.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<DiaryResponse> createDiary(@RequestBody DiaryRequest request, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getId();

        DiaryResponse response = diaryService.createDiary(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @Operation(summary = "일기 목록 조회")
    @GetMapping("/diaries")
    public ResponseEntity<List<DiaryResponse>> getDiaries(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getId();

        List<DiaryResponse> diaries = diaryService.getDiaries(userId);
        return ResponseEntity.ok(diaries);
    }


    @Operation(summary = "단일 일기 조회")
    @GetMapping("/diaries/{diaryId}")
    public ResponseEntity<DiaryResponse> getDiary(@PathVariable Long diaryId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getId();

        DiaryResponse response = diaryService.getDiary(userId, diaryId);
        return ResponseEntity.ok(response);
    }

}
