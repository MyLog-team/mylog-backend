package mylog_backend.mylog.videoRecommend;

import lombok.Builder;
import lombok.Getter;

@Getter
public class VideoResponse {

    private String title;
    private String videoId;
    private String thumbnailUrl;

    @Builder
    public VideoResponse(String title, String videoId, String thumbnailUrl) {
        this.title = title;
        this.videoId = videoId;
        this.thumbnailUrl = thumbnailUrl;
    }

    private static VideoResponse toYoutubeVideo(String message, String title, String videoId, String thumbnailUrl) {
        return VideoResponse.builder()
                .title(title)
                .videoId(videoId)
                .thumbnailUrl(thumbnailUrl)
                .build();

    }
}
