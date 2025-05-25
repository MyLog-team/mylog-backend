package mylog_backend.mylog.videoRecommend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
//@AllArgsConstructor
public class VideoResponse {

    private String title;
    private String videoId;
    private String thumbnailUrl;

    @Builder
    public VideoResponse(YoutubeVideo video) {
        this.title = video.getTitle();
        this.videoId = video.getVideoId();
        this.thumbnailUrl = video.getThumbnailUrl();
    }
}
