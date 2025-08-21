package mylog_backend.mylog.diary;

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
class DiaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private User testUser;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        // 테스트용 유저 생성
        testUser = User.builder()
                .loginId("testUser1")
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
    @DisplayName("일기 생성 API 테스트")
    void createDiary() throws Exception {
        // given
        DiaryRequest request = DiaryRequest.builder()
                .diaryTitle("테스트 일기")
                .diaryContent("오늘은 좋은 하루였다")
                .feeling(Feeling.HAPPY)
                .feelingScore(80)
                .isPublic(IsPublic.PRIVATE)
                .build();

        // when & then
        mockMvc.perform(post("/api/diaries")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diaryId").exists())
                .andExpect(jsonPath("$.dairyTitle").value("테스트 일기"))
                .andExpect(jsonPath("$.dairyContent").value("오늘은 좋은 하루였다"))
                .andExpect(jsonPath("$.feeling").value("HAPPY"))
                .andExpect(jsonPath("$.feelingScore").value(80))
                .andExpect(jsonPath("$.isPublic").value("PRIVATE"));
    }

    @Test
    @DisplayName("일기 조회 API 테스트")
    void getDiary() throws Exception {
        // given
        Diary diary = Diary.builder()
                .dairyTitle("조회 테스트 일기")
                .dairyContent("테스트 내용")
                .feeling(Feeling.SAD)
                .feelingScore(30)
                .isPublic(IsPublic.PUBLIC)
                .user(testUser)
                .build();
        
        Diary savedDiary = diaryRepository.save(diary);

        // when & then
        mockMvc.perform(get("/api/diaries/{diaryId}", savedDiary.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diaryId").value(savedDiary.getId()))
                .andExpect(jsonPath("$.dairyTitle").value("조회 테스트 일기"))
                .andExpect(jsonPath("$.dairyContent").value("테스트 내용"))
                .andExpect(jsonPath("$.feeling").value("SAD"))
                .andExpect(jsonPath("$.feelingScore").value(30))
                .andExpect(jsonPath("$.isPublic").value("PUBLIC"));
    }

    @Test
    @DisplayName("일기 목록 조회 API 테스트")
    void getAllDiaries() throws Exception {
        // given - 테스트 일기들 생성
        Diary diary1 = Diary.builder()
                .dairyTitle("일기 1")
                .dairyContent("내용 1")
                .feeling(Feeling.HAPPY)
                .feelingScore(70)
                .isPublic(IsPublic.PUBLIC)
                .user(testUser)
                .build();
        
        Diary diary2 = Diary.builder()
                .dairyTitle("일기 2")
                .dairyContent("내용 2")
                .feeling(Feeling.NOT_BAD)
                .feelingScore(50)
                .isPublic(IsPublic.PRIVATE)
                .user(testUser)
                .build();

        diaryRepository.save(diary1);
        diaryRepository.save(diary2);

        // when & then
        mockMvc.perform(get("/api/diaries")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("존재하지 않는 일기 조회시 404 반환")
    void getDiary_NotFound() throws Exception {
        // when & then
        mockMvc.perform(get("/api/diaries/{diaryId}", 999L)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("잘못된 일기 생성 요청시 400 반환")
    void createDiary_BadRequest() throws Exception {
        // given - 잘못된 요청 (필수 필드 누락)
        DiaryRequest request = DiaryRequest.builder()
                .diaryTitle("")  // 빈 제목
                .diaryContent("내용")
                .feeling(Feeling.HAPPY)
                .feelingScore(150)  // 잘못된 점수 (100 초과)
                .isPublic(IsPublic.PRIVATE)
                .build();

        // when & then
        mockMvc.perform(post("/api/diaries")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("인증 토큰 없이 API 호출시 401 반환")
    void createDiary_Unauthorized() throws Exception {
        // given
        DiaryRequest request = DiaryRequest.builder()
                .diaryTitle("테스트 일기")
                .diaryContent("내용")
                .feeling(Feeling.HAPPY)
                .feelingScore(80)
                .isPublic(IsPublic.PRIVATE)
                .build();

        // when & then
        mockMvc.perform(post("/api/diaries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
