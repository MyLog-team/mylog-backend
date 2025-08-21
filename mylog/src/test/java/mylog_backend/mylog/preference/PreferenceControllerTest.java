package mylog_backend.mylog.preference;

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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class PreferenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PreferenceRepository preferenceRepository;

    @Autowired
    private JwtUtil jwtUtil;

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
    @DisplayName("선호도 설정 API 테스트")
    void setPreference() throws Exception {
        // given
        PreferenceRequest request = PreferenceRequest.builder()
                .tag1("음악")
                .tag2("여행")
                .build();

        // when & then
        mockMvc.perform(post("/api/preferences")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("선호도 조회 API 테스트")
    void getPreference() throws Exception {
        // given - 두 개의 선호도 태그 생성
        Preference preference1 = Preference.builder()
                .tag("독서")
                .user(testUser)
                .build();
        
        Preference preference2 = Preference.builder()
                .tag("영화")
                .user(testUser)
                .build();
        
        preferenceRepository.save(preference1);
        preferenceRepository.save(preference2);

        // when & then
        mockMvc.perform(get("/api/preferences")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("선호도 수정 API 테스트")
    void updatePreference() throws Exception {
        // given - 기존 선호도 생성
        Preference preference1 = Preference.builder()
                .tag("독서")
                .user(testUser)
                .build();
        
        Preference preference2 = Preference.builder()
                .tag("영화")
                .user(testUser)
                .build();
        
        preferenceRepository.save(preference1);
        preferenceRepository.save(preference2);

        // 수정할 내용
        PreferenceRequest updateRequest = PreferenceRequest.builder()
                .tag1("스포츠")
                .tag2("음악")
                .build();

        // when & then
        mockMvc.perform(put("/api/preferences")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("선호도가 없을 때 조회시 404 반환")
    void getPreference_NotFound() throws Exception {
        // when & then
        mockMvc.perform(get("/api/preferences")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("잘못된 선호도 설정 요청시 400 반환")
    void setPreference_BadRequest() throws Exception {
        // given - 빈 태그들
        PreferenceRequest request = PreferenceRequest.builder()
                .tag1("")
                .tag2("")
                .build();

        // when & then
        mockMvc.perform(post("/api/preferences")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("인증 토큰 없이 API 호출시 401 반환")
    void setPreference_Unauthorized() throws Exception {
        // given
        PreferenceRequest request = PreferenceRequest.builder()
                .tag1("음악")
                .tag2("여행")
                .build();

        // when & then
        mockMvc.perform(post("/api/preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
