package mylog_backend.mylog.videoRecommend;

import mylog_backend.mylog.preference.Preference;
import mylog_backend.mylog.preference.PreferenceRepository;
import mylog_backend.mylog.user.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VideoRecommendServiceTest {

    private PreferenceRepository preferenceRepository;
    private YoutubeClient youtubeClient;
    private VideoRecommendService videoRecommendService;

    /**
     * 테스트용 환경을 mock으로 구축
     */
    @BeforeEach
    void setUp() {
        preferenceRepository = mock(PreferenceRepository.class);
        youtubeClient = mock(YoutubeClient.class);
        videoRecommendService = new VideoRecommendService(preferenceRepository, youtubeClient);
    }

    @Test
    void recommend_shouldReturnVideoResponse() {
        // Given
        Long userId = 1L;
        String mood = "study";

        User mockUser = mock(User.class);
        Preference p1 = Preference.builder()
                .tag("lofi")
                .user(mockUser)
                .build();

        Preference p2 = Preference.builder()
                .tag("classic")
                .user(mockUser)
                .build();

        List<Preference> mockPreferences = List.of(p1, p2);

        when(preferenceRepository.findByUserId(userId)).thenReturn(mockPreferences);

        // 유튜브 클라이언트 mock 결과
        List<YoutubeVideo> mockVideos = Arrays.asList(
                new YoutubeVideo("Relaxing Lofi", "abc123", "http://thumbnail.url")
        );

        // 검색 쿼리 수정: 태그 2개 + 무드 + "music"
        when(youtubeClient.search("lofi classic study music")).thenReturn(mockVideos);

        // When
        VideoResponse response = videoRecommendService.recommend(userId, mood);

        // Then
        assertNotNull(response);
        assertEquals("Relaxing Lofi", response.getTitle());
        assertEquals("abc123", response.getVideoId());
        assertEquals("http://thumbnail.url", response.getThumbnailUrl());
    }

}
