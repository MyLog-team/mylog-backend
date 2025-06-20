package mylog_backend.mylog.auth;

import mylog_backend.mylog.config.EmbeddedRedisConfig;
import mylog_backend.mylog.user.User;
import mylog_backend.mylog.user.UserRepository;
import mylog_backend.mylog.common.exception.BadCredentialsException;
import mylog_backend.mylog.common.exception.DuplicateLoginIdException;
import mylog_backend.mylog.common.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Import(EmbeddedRedisConfig.class)
@Transactional
class JwtServiceTest {

    @Autowired private JwtService jwtService;
    @Autowired private UserRepository userRepository;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private JwtProvider jwtProvider;

    private SignupRequest signupRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // 테스트용 회원가입 데이터
        signupRequest = SignupRequest.builder()
                .loginId("testuser")
                .password("password123")
                .userName("테스트유저")
                .build();

        // 테스트용 로그인 데이터
        loginRequest = LoginRequest.builder()
                .loginId("testuser")
                .password("password123")
                .build();
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signupSuccess() {
        // when
        SignupResponse response = jwtService.signup(signupRequest);

        // then
        assertThat(response.getMessage()).isEqualTo("회원가입되었습니다.");
        assertThat(response.getUserId()).isNotNull();
        
        // DB에 저장되었는지 확인
        User savedUser = userRepository.findByLoginId(signupRequest.getLoginId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        assertThat(savedUser.getLoginId()).isEqualTo(signupRequest.getLoginId());
        assertThat(savedUser.getUserName()).isEqualTo(signupRequest.getUserName());
    }

    @Test
    @DisplayName("중복된 로그인 아이디로 회원가입 실패 테스트")
    void signupWithDuplicateLoginId() {
        // given
        jwtService.signup(signupRequest);

        // when & then
        assertThatThrownBy(() -> jwtService.signup(signupRequest))
                .isInstanceOf(DuplicateLoginIdException.class)
                .hasMessageContaining("이미 사용 중인 아이디입니다");
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void loginSuccess() {
        // given
        jwtService.signup(signupRequest);

        // when
        LoginResponse response = jwtService.login(loginRequest);

        // then
        assertThat(response.getMessage()).isEqualTo("로그인에 성공하였습니다.");
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getRefreshToken()).isNotNull();
        assertThat(response.getGrantType()).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 로그인 실패 테스트")
    void loginWithNonExistentId() {
        // when & then
        assertThatThrownBy(() -> jwtService.login(loginRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("존재하지 않는 아이디입니다");
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 실패 테스트")
    void loginWithWrongPassword() {
        // given
        jwtService.signup(signupRequest);
        LoginRequest wrongPasswordRequest = LoginRequest.builder()
                .loginId("testuser")
                .password("wrongpassword")
                .build();

        // when & then
        assertThatThrownBy(() -> jwtService.login(wrongPasswordRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("비밀번호가 일치하지 않습니다");
    }

    @Test
    @DisplayName("로그아웃 테스트")
    void logout() {
        // given
        jwtService.signup(signupRequest);
        LoginResponse loginResponse = jwtService.login(loginRequest);
        String accessToken = loginResponse.getAccessToken();

        // when
        jwtService.logout(accessToken);

        // then
        assertThat(jwtProvider.validateToken(accessToken)).isFalse();
    }
}
