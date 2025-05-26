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
    // 유튜브 클라이언트
    private final YoutubeClient youtubeClient;

    public VideoResponse recommend(Long userId, String mood) {
        List<Preference> preferences = preferenceRepository.findByUserId(userId);

        if (preferences.isEmpty()) {
            throw new IllegalStateException("선호 태그가 1개 이상 필요합니다.");
        }

        String query;
        if (preferences.size() == 1) {
            String tag1 = preferences.get(0).getTag();
            query = String.join(" ", tag1, mood, "music");
        } else {
            String tag1 = preferences.get(0).getTag();
            String tag2 = preferences.get(1).getTag();
            query = String.join(" ", tag1, tag2, mood, "music");
        }

        List<YoutubeVideo> videos = youtubeClient.search(query);

        if (videos.isEmpty()) {
            throw new RuntimeException("추천할 영상이 없습니다.");
        }

        // 결과 확인용 로그
        System.out.println("검색 쿼리: " + query);
        System.out.println("검색된 비디오 개수: " + videos.size());
        for (YoutubeVideo v : videos) {
            System.out.println(" - " + v.getTitle() + ", id: " + v.getVideoId());
        }

        YoutubeVideo selected = selectOneRandomly(videos);

        return new VideoResponse(selected);
    }


    /**
     * 2. 추천 영상이 없을때 예외 처리 로직
     * @param videos
     * @return
     */
    private YoutubeVideo selectOneRandomly(List<YoutubeVideo> videos) {
        if (videos.isEmpty()) {
            throw new RuntimeException("추천할 영상이 없습니다.");
        }
        return videos.get(new Random().nextInt(videos.size()));
    }
}
