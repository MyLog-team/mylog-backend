package mylog_backend.mylog.videoRecommend;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 유튜브 API 요청 결과에 대한 응답을 담는 DTO
@Getter
@AllArgsConstructor
public class YoutubeVideo {

    private String title;
    private String videoId;
    private String thumbnailUrl;
}
