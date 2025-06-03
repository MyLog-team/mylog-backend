package mylog_backend.mylog.videoRecommend;

import lombok.RequiredArgsConstructor;
import mylog_backend.mylog.preference.Preference;
import mylog_backend.mylog.preference.PreferenceRepository;
import mylog_backend.mylog.user.User;
import mylog_backend.mylog.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;


//@Tag("external-integration") // CI에서 빌드시 해당 파일 제외
/**
 * 실제 유튜브 API로 테스트
 */
@RequiredArgsConstructor
@SpringBootTest
@ActiveProfiles("test")
public class VideoRecommendIntegrationTest {

    // 생성자 주입법으로 필요한 도메인 주입
    @Autowired
    private VideoRecommendService videoRecommendService;

    @Autowired
    private YoutubeClient youtubeClient;

    @Autowired
    private PreferenceRepository preferenceRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    @DisplayName("사용자의 선호 태그에 따라서 유튜브 영상을 추천받는다.")
    void recommend_shouldReturnRealVideoResponse() {

        // given
        // 테스트용 유저 생성
        User user = userRepository.save(User.builder()
                .email("test@example.com")
                .loginId("testLogin")  // ✅ 필수
                .password("dummyPassword")  // 예시로 같이 채워줌
                .userName("Tester")         // 다른 NOT NULL 필드도 같이
                .provider("local")
                .providerId("test")
                .build());

        // 테스트용 선호 태그 세팅
        Preference p1 = preferenceRepository.save(Preference.builder()
                .tag("lofi")
                .user(user)
                .build());

        Preference p2 = preferenceRepository.save(Preference.builder()
                .tag("classic")
                .user(user)
                .build());

        // 유저의 선호 태그도 실제 DB에서 가져오거나 직접 세팅
        List<Preference> realPreferences = List.of(p1, p2);

        // When
        // 유튜브 영상 추천 로직 실행
        VideoResponse response = videoRecommendService.recommend(user.getId(), "study");


        // Then
        //  실제 영상의 제목, 비디오 아이디, 썸네일 출력
        System.out.println("Title: " + response.getTitle());
        System.out.println("VideoId: " + response.getVideoId());
        System.out.println("Thumbnail: " + response.getThumbnailUrl());

        assertNotNull(response);
    }

}
