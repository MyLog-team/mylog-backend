package mylog_backend.mylog.videoRecommend;

import com.fasterxml.jackson.databind.ObjectMapper;
import mylog_backend.mylog.auth.JWToken;
import mylog_backend.mylog.auth.JwtUtil;
import mylog_backend.mylog.user.User;
import mylog_backend.mylog.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class VideoRecommendControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public VideoRecommendService videoRecommendService() {
            return mock(VideoRecommendService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private VideoRecommendService videoRecommendService;

    private User testUser;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        // Mock 재설정
        reset(videoRecommendService);
        
        // 테스트용 유저 생성
        testUser = User.builder()
                .loginId("testUser123")
                .email("test@example.com")
                .password("encoded-password")
                .userName("테스트유저")
                .build();
        userRepository.save(testUser);

        // JWT 토큰 생성
        JWToken token = jwtUtil.generateToken(testUser.getId());
        jwtToken = token.getAccessToken();
    }

    @Test
    @DisplayName("비디오 추천 API 테스트")
    void recommendVideos() throws Exception {
        // given
        MoodRequest request = MoodRequest.builder()
                .mood("happy")
                .build();

        VideoResponse mockResponse = VideoResponse.builder()
                .videoId("test-video-id")
                .title("Test Video")
                .thumbnailUrl("https://test.com/thumbnail.jpg")
                .build();

        when(videoRecommendService.recommend(anyLong(), any(String.class)))
                .thenReturn(mockResponse);

        // when & then
        mockMvc.perform(post("/api/video-recommend")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.videoId").value("test-video-id"))
                .andExpect(jsonPath("$.title").value("Test Video"))
                .andExpect(jsonPath("$.thumbnailUrl").value("https://test.com/thumbnail.jpg"));
    }

    @Test
    @DisplayName("잘못된 기분 상태로 추천 요청시 400 반환")
    void recommendVideos_BadRequest() throws Exception {
        // given - 잘못된 요청 (빈 mood)
        MoodRequest request = MoodRequest.builder()
                .mood("")
                .build();

        // when & then
        mockMvc.perform(post("/api/video-recommend")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("서비스에서 예외 발생시 500 반환")
    void recommendVideos_InternalServerError() throws Exception {
        // given
        MoodRequest request = MoodRequest.builder()
                .mood("sad")
                .build();

        when(videoRecommendService.recommend(anyLong(), any(String.class)))
                .thenThrow(new RuntimeException("YouTube API 오류"));

        // when & then
        mockMvc.perform(post("/api/video-recommend")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("인증 토큰 없이 API 호출시 401 반환")
    void recommendVideos_Unauthorized() throws Exception {
        // given
        MoodRequest request = MoodRequest.builder()
                .mood("happy")
                .build();

        // when & then
        mockMvc.perform(post("/api/video-recommend")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("다양한 기분 상태에 대한 추천 테스트")
    void recommendVideos_DifferentMoods() throws Exception {
        // given
        VideoResponse mockResponse = VideoResponse.builder()
                .videoId("sad-video-id")
                .title("Sad Video")
                .thumbnailUrl("https://test.com/sad.jpg")
                .build();

        when(videoRecommendService.recommend(anyLong(), any(String.class)))
                .thenReturn(mockResponse);

        // Test SAD mood
        MoodRequest sadRequest = MoodRequest.builder()
                .mood("sad")
                .build();

        mockMvc.perform(post("/api/video-recommend")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sadRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.videoId").value("sad-video-id"));

        // Test calm mood
        MoodRequest normalRequest = MoodRequest.builder()
                .mood("calm")
                .build();

        mockMvc.perform(post("/api/video-recommend")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(normalRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.videoId").value("sad-video-id"));
    }
}
