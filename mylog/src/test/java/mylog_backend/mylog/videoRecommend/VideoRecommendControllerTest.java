package mylog_backend.mylog.videoRecommend;

import com.fasterxml.jackson.databind.ObjectMapper;
import mylog_backend.mylog.auth.JWToken;
import mylog_backend.mylog.auth.JwtUtil;
import mylog_backend.mylog.diary.Feeling;
import mylog_backend.mylog.user.User;
import mylog_backend.mylog.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class VideoRecommendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @MockBean
    private VideoRecommendService videoRecommendService;

    private User testUser;
    private String jwtToken;

    @BeforeEach
    void setUp() {
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
                .mood(Feeling.HAPPY)
                .build();

        VideoResponse mockResponse = VideoResponse.builder()
                .videoId("test-video-id")
                .title("Test Video")
                .channelTitle("Test Channel")
                .thumbnailUrl("https://test.com/thumbnail.jpg")
                .build();

        when(videoRecommendService.recommend(anyLong(), any(MoodRequest.class)))
                .thenReturn(Arrays.asList(mockResponse));

        // when & then
        mockMvc.perform(post("/api/video-recommend")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].videoId").value("test-video-id"))
                .andExpect(jsonPath("$[0].title").value("Test Video"))
                .andExpect(jsonPath("$[0].channelTitle").value("Test Channel"));
    }

    @Test
    @DisplayName("잘못된 기분 상태로 추천 요청시 400 반환")
    void recommendVideos_BadRequest() throws Exception {
        // given - 잘못된 요청 (null mood)
        MoodRequest request = MoodRequest.builder()
                .mood(null)
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
                .mood(Feeling.SAD)
                .build();

        when(videoRecommendService.recommend(anyLong(), any(MoodRequest.class)))
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
                .mood(Feeling.HAPPY)
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
                .channelTitle("Channel")
                .thumbnailUrl("https://test.com/sad.jpg")
                .build();

        when(videoRecommendService.recommend(anyLong(), any(MoodRequest.class)))
                .thenReturn(Arrays.asList(mockResponse));

        // Test SAD mood
        MoodRequest sadRequest = MoodRequest.builder()
                .mood(Feeling.SAD)
                .build();

        mockMvc.perform(post("/api/video-recommend")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sadRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // Test NOT_BAD mood
        MoodRequest normalRequest = MoodRequest.builder()
                .mood(Feeling.NOT_BAD)
                .build();

        mockMvc.perform(post("/api/video-recommend")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(normalRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
