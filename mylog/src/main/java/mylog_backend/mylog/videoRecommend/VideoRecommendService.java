package mylog_backend.mylog.videoRecommend;

import lombok.RequiredArgsConstructor;
import mylog_backend.mylog.common.exception.VideoNotFoundException;
import mylog_backend.mylog.preference.Preference;
import mylog_backend.mylog.preference.PreferenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class VideoRecommendService {

    private final PreferenceRepository preferenceRepository;
    private final YoutubeClient youtubeClient;


    /**
     * 영상 추천
     * @param userId : 요청한 사용자 아이디
     * @param mood : 사용자가 입력한 상황, 일기 작성히 듣고 싶은 음악의 분위기
     * @return : selectOneRandomly()를 이용해 영상 하나를 반환
     */
    public VideoResponse recommend(Long userId, String mood) {
        // 1. 사용자의 선호 태그를 userId로 찾아와 할당받음
        List<Preference> preferences = preferenceRepository.findByUserId(userId);


        // 2. 요청 쿼리 구성
        String query;

        // 태그가 없으면 요청 불가
        if (preferences.isEmpty()){
            throw new IllegalStateException("선호 태그가 1개 이상 필요합니다.");
        }
        // 1) 태그가 1개이면, 태그 1개로 요청 쿼리 구성
        else if (preferences.size() == 1) {
            String tag1 = preferences.get(0).getTag();
            query = String.join(" ", tag1, mood, "music");
        }
        // 2) 태그가 1개가 아니면, 1, 2번째 태그들로 쿼리 구성
        else {
            String tag1 = preferences.get(0).getTag();
            String tag2 = preferences.get(1).getTag();
            query = String.join(" ", tag1, tag2, mood, "music"); // delimiter: 구분자
            // query : "tag1 tag2 music"
        }

        // 3. 유튜브 클라이언트를 통해 API에 요청을 보냄
        List<VideoResponse> videos = youtubeClient.search(query);


//         결과 확인용 로그
        System.out.println("검색 쿼리: " + query);
        System.out.println("검색된 비디오 개수: " + videos.size());
        for (VideoResponse v : videos) {
            System.out.println(" - " + v.getTitle() + ", id: " + v.getVideoId());
        }

        VideoResponse selected = selectOneRandomly(videos);

        return selected;
    }


    /**
     * 추천 영상이 없을때 예외 처리 로직
     * @param videos : 유튜브 API 요청후 결과로 담아둔 영상 5개의 정보
     * @return : 5개 영상중 랜덤한 영상을 하나 반환
     */
    private VideoResponse selectOneRandomly(List<VideoResponse> videos) {
        // 영상 리스트가 비어있는지 검증
        if (videos.isEmpty()) {
            throw new VideoNotFoundException("추천할 영상이 없습니다.");
        }
        // 5개 영상중 하나를 랜덤하게 골라 반환
        return videos.get(new Random().nextInt(videos.size()));
    }
}
