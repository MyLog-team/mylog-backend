package mylog_backend.mylog.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import mylog_backend.mylog.config.EmbeddedRedisConfig;
import mylog_backend.mylog.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get; // protected API 테스트를 위해 get 요청 추가


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//@Tag("external-integration") // CI에서 빌드시 해당 파일 제외
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@Import(EmbeddedRedisConfig.class)
class AuthControllerTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate; // 혹은 StringRedisTemplate

    // spring MVC 애플리케이션의 웹 계층을 실제 HTTP 요청 없이 테스트 가능
    @Autowired
    private MockMvc mockMvc;

    // Jackson 라이브러리가 제공하는 클래스
    // 자바 객체를 JSON 문자열로 변환(직렬화) 혹은 그 역의 변환(역직렬화)를 한다.
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        // ✅ Redis 데이터 초기화
        redisTemplate.getConnectionFactory().getConnection().flushAll();

    }

    @Test
    @DisplayName("회원가입 성공")
    void signupSuccess() throws Exception {
        // given
        SignupRequest request = SignupRequest.builder()
                .loginId("testuser")
                .password("password123")
                .userName("테스트유저")
                .build();

        // when
        // 실제로 /auth/signup으로 post 요청을 보낸것처럼 행동
        ResultActions result = mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                // 요청들을 JSON 형태로 변환
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 아이디")
    void signupFailDuplicateId() throws Exception {
        // given
        SignupRequest request = SignupRequest.builder()
                .loginId("testuser")
                .password("password123")
                .userName("테스트유저")
                .build();

        // 이미 존재하는 사용자 생성
        jwtService.signup(request);

        // when
        ResultActions result = mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isConflict());
    }

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() throws Exception {
        // given
        SignupRequest signupRequest = SignupRequest.builder()
                .loginId("testuser")
                .password("password123")
                .userName("테스트유저")
                .build();
        jwtService.signup(signupRequest);

        LoginRequest loginRequest = LoginRequest.builder()
                .loginId("testuser")
                .password("password123")
                .build();

        // when
        ResultActions result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.grantType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
//                .andExpect(jsonPath("$.userName").value("테스트유저"));
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void loginFailWrongPassword() throws Exception {
        // given
        SignupRequest signupRequest = SignupRequest.builder()
                .loginId("testuser")
                .password("password123")
                .userName("테스트유저")
                .build();
        jwtService.signup(signupRequest);

        LoginRequest loginRequest = LoginRequest.builder()
                .loginId("testuser")
                .password("wrongpassword")
                .build();

        // when
        ResultActions result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 아이디")
    void loginFailNonExistentId() throws Exception {
        // given
        LoginRequest loginRequest = LoginRequest.builder()
                .loginId("nonexistent")
                .password("password123")
                .build();

        // when
        ResultActions result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        // then
        result.andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("로그아웃 성공 및 토큰 무효화 확인")
    void logoutSuccess() throws Exception {
        // given
        // 1. 회원가입 및 로그인하여 Access Token 발급
        SignupRequest signupRequest = SignupRequest.builder()
                .loginId("logoutuser")
                .password("logoutpass")
                .userName("로그아웃유저")
                .build();
        jwtService.signup(signupRequest);

        LoginRequest loginRequest = LoginRequest.builder()
                .loginId("logoutuser")
                .password("logoutpass")
                .build();

        ResultActions loginResult = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        // ⭐⭐⭐ 이 부분을 수정합니다: JSON 응답 본문에서 Access Token 추출 ⭐⭐⭐
        String loginResponseContent = loginResult.andExpect(status().isOk()) // 로그인 성공 확인
                .andExpect(jsonPath("$.accessToken").exists()) // accessToken 존재 확인
                .andReturn().getResponse().getContentAsString(); // 응답 본문 전체를 String으로 가져옴

        // LoginResponse DTO와 매핑하여 accessToken 필드를 직접 가져옵니다.
        // LoginResponse DTO가 어떻게 생겼는지에 따라 달라질 수 있습니다.
        // 예를 들어 LoginResponse가 다음과 같다고 가정합니다.
        // public class LoginResponse { private String accessToken; ... }
        LoginResponse loginResponse = objectMapper.readValue(loginResponseContent, LoginResponse.class);
        String actualAccessToken = loginResponse.getAccessToken();

        // when
        // 2. 로그아웃 요청 (Access Token을 Authorization 헤더에 포함)
        ResultActions logoutResult = mockMvc.perform(post("/auth/logout")
                .header("Authorization", "Bearer " + actualAccessToken) // 추출한 토큰 사용
                .contentType(MediaType.APPLICATION_JSON)); // 또는 빈 본문으로 보내도 무방 (컨트롤러 구현에 따라)

        // then
        // 3. 로그아웃 요청 성공 확인
        logoutResult.andExpect(status().isOk());

        // 4. 로그아웃된 토큰으로 보호된 리소스 접근 시도
        // (주의: "/api/some-protected-resource"는 임시 예시입니다. 실제 보호된 API 엔드포인트를 사용하세요.)
        // 또한, 이 테스트를 통과하려면 JwtUtil (또는 JwtProvider)의 validateToken 메서드에
        // Redis 블랙리스트 검사 로직이 반드시 추가되어 있어야 합니다.
        ResultActions protectedApiAccessResult = mockMvc.perform(get("/protected-resource")
                .header("Authorization", "Bearer " + actualAccessToken));

        // 5. 접근 거부 응답 확인 (401 Unauthorized 또는 403 Forbidden)
        protectedApiAccessResult.andExpect(status().isForbidden()); // 또는 isForbidden()
    }
}


