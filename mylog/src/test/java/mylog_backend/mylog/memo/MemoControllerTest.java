package mylog_backend.mylog.memo;

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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class MemoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemoRepository memoRepository;

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
    @DisplayName("메모 생성 API 테스트")
    void createMemo() throws Exception {
        // given
        MemoRequest request = MemoRequest.builder()
                .memoContent("테스트 메모 내용")
                .build();

        // when & then
        mockMvc.perform(post("/api/memos")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memoId").exists())
                .andExpect(jsonPath("$.memoContent").value("테스트 메모 내용"));
    }

    @Test
    @DisplayName("메모 목록 조회 API 테스트")
    void getVisibleMemos() throws Exception {
        // given - 테스트 메모들 생성
        Memo memo1 = Memo.builder()
                .memoContent("메모 1")
                .user(testUser)
                .isVisible(IsVisible.VISIBLE)
                .build();
        
        Memo memo2 = Memo.builder()
                .memoContent("메모 2")
                .user(testUser)
                .isVisible(IsVisible.VISIBLE)
                .build();

        memoRepository.saveAll(Arrays.asList(memo1, memo2));

        // when & then
        mockMvc.perform(get("/api/memos")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("단일 메모 조회 API 테스트")
    void getMemo() throws Exception {
        // given
        Memo memo = Memo.builder()
                .memoContent("단일 조회 테스트 메모")
                .user(testUser)
                .isVisible(IsVisible.VISIBLE)
                .build();
        
        Memo savedMemo = memoRepository.save(memo);

        // when & then
        mockMvc.perform(get("/api/memos/{memoId}", savedMemo.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memoId").value(savedMemo.getId()))
                .andExpect(jsonPath("$.memoContent").value("단일 조회 테스트 메모"));
    }

    @Test
    @DisplayName("메모 체크 API 테스트")
    void checkedMemo() throws Exception {
        // given
        Memo memo = Memo.builder()
                .memoContent("체크될 메모")
                .user(testUser)
                .isVisible(IsVisible.VISIBLE)
                .isChecked(IsChecked.UNCHECKED)
                .build();
        
        Memo savedMemo = memoRepository.save(memo);

        // when & then
        mockMvc.perform(patch("/api/memos/{memoId}/check", savedMemo.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isChecked").value("CHECKED"))
                .andExpect(jsonPath("$.isVisible").value("HIDDEN"));
    }

    @Test
    @DisplayName("존재하지 않는 메모 조회시 404 반환")
    void getMemo_NotFound() throws Exception {
        // when & then
        mockMvc.perform(get("/api/memos/{memoId}", 999L)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("인증 토큰 없이 API 호출시 401 반환")
    void createMemo_Unauthorized() throws Exception {
        // given
        MemoRequest request = MemoRequest.builder()
                .memoContent("테스트 메모")
                .build();

        // when & then
        mockMvc.perform(post("/api/memos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
