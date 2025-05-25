package mylog_backend.mylog.videoRecommend;

import lombok.Builder;
import lombok.Getter;

// 분위기를 입력받았을때 담는 DTO
@Getter
public class MoodRequest {
    private String mood;

    @Builder
    public MoodRequest(String mood) {
        this.mood = mood;
    }
}
