package mylog_backend.mylog.videoRecommend;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VideoRecommendController {

    private final VideoRecommendService videoRecommendService;

    /**
     * 1. 동영상 추천 API
     * @param userId : 사용자 아이디
     * @param request : 사용자에게 분위기를 입력받음
     * @return : 생성완료
     */
    @PostMapping("/users/{user_id}/recommendations")
    public ResponseEntity<VideoResponse> recommendVideo(@PathVariable("user_id") Long userId, @RequestBody MoodRequest request) {
        VideoResponse response = videoRecommendService.recommend(userId, request.getMood());
        return ResponseEntity.ok(response);
    }
}
