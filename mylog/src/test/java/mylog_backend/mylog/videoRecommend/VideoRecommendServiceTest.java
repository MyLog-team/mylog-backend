package videoRecommend;

import mylog_backend.mylog.preference.Preference;
import mylog_backend.mylog.preference.PreferenceRepository;
import mylog_backend.mylog.user.User;
import mylog_backend.mylog.videoRecommend.VideoRecommendService;
import mylog_backend.mylog.videoRecommend.VideoResponse;
import mylog_backend.mylog.videoRecommend.YoutubeClient;
import mylog_backend.mylog.videoRecommend.YoutubeVideo;
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

        when(youtubeClient.search("lofi study relax music")).thenReturn(mockVideos);

        // When
        VideoResponse response = videoRecommendService.recommend(userId, mood);

        // Then
        assertNotNull(response);
        assertEquals("Relaxing Lofi", response.getTitle());
        assertEquals("abc123", response.getVideoId());
        assertEquals("http://thumbnail.url", response.getThumbnailUrl());
    }

    @Test
    void recommend_shouldThrow_whenNotEnoughPreferences() {
        Long userId = 1L;
        User mockUser = mock(User.class);

        Preference pref = Preference.builder()
                .tag("focus")
                .build();
        pref.setUser(mockUser);

        // 1개만 있는 경우
        when(preferenceRepository.findByUserId(userId)).thenReturn(List.of(pref));


        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> videoRecommendService.recommend(userId, "calm")
        );

        assertEquals("선호 태그가 2개 이상 필요합니다.", ex.getMessage());
    }

    @Test
    void recommend_shouldThrow_whenNoVideosFound() {
        Long userId = 1L;
        User mockUser = mock(User.class);

        Preference pref = Preference.builder()
                .tag("focus")
                .user(mockUser)
                .build();

        when(preferenceRepository.findByUserId(userId)).thenReturn(List.of(pref));

        when(youtubeClient.search("focus jazz chill music")).thenReturn(List.of());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            videoRecommendService.recommend(userId, "chill");
        });

        assertEquals("추천할 영상이 없습니다.", ex.getMessage());
    }
}
