package mylog_backend.mylog.videoRecommend;

import lombok.RequiredArgsConstructor;
import mylog_backend.mylog.preference.Preference;
import mylog_backend.mylog.preference.PreferenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class VideoRecommendService {

    // 선호 태그 레포지토리
    private final PreferenceRepository preferenceRepository;
    //
    private final YoutubeClient youtubeClient;

    public VideoResponse recommend(Long userId, String mood) {
        List<Preference> preferences = preferenceRepository.findByUserId(userId);

        // 태그 2개 추출
        String tag1 = preferences.get(0).getTag();
        String tag2 = preferences.get(1).getTag();

        String query = String.join(" ", tag1, tag2, mood, "music");

        List<YoutubeVideo> videos = youtubeClient.search(query);

        YoutubeVideo selected = selectOneRandomly(videos);

        return new VideoResponse(selected);
    }

    private YoutubeVideo selectOneRandomly(List<YoutubeVideo> videos) {
        if (videos.isEmpty()) {
            throw new RuntimeException("추천할 영상이 없습니다.");
        }
        return videos.get(new Random().nextInt(videos.size()));
    }
}
