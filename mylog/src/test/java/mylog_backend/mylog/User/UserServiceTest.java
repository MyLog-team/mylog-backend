package mylog_backend.mylog.User;

import mylog_backend.mylog.auth.LoginRequest;
import mylog_backend.mylog.auth.LoginResponse;
import mylog_backend.mylog.auth.SignupRequest;
import mylog_backend.mylog.user.User;
import mylog_backend.mylog.user.UserRepository;
import mylog_backend.mylog.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void setUp() {
        userRepository.deleteAll(); // 이전 데이터 삭제

        // 회원가입 사전 등록
        SignupRequest signupRequest = SignupRequest.builder()
                .loginId("testuser")
                .password("1234")
//                .email("test@example.com")
                .userName("테스트유저")
                .build();

        userService.signup(signupRequest);
    }

    @Test
    @Transactional
    @DisplayName("회원가입 로직 테스트")
    void signupSuccess() {
        // given
        SignupRequest request = SignupRequest.builder()
                .loginId("mylog123")
                .userName("정대식")
                .password("plain_password")
                .build();

        // when
        userService.signup(request);

        // then
        User savedUser = userRepository.findByLoginId("mylog123").orElseThrow();
        assertThat(savedUser.getUserName()).isEqualTo("정대식");
        assertThat(passwordEncoder.matches("plain_password", savedUser.getPassword())).isTrue();
    }


    @Test
    @Transactional
    @DisplayName("중복 로그인 아이디가 있으면 예외가 발생한다")
    void signupDuplicateLoginId() {
        // given
        SignupRequest request = SignupRequest.builder()
                .loginId("mylog123")
                .userName("정대식")
                .password("plain_password")
                .build();

        userService.signup(request);

        // when & then
        assertThatThrownBy(() -> userService.signup(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 사용 중인 아이디입니다.");
    }


//    @Test
//    @Transactional
//    @DisplayName("회원가입 후 로그인에 성공한다")
//    void signupAndLoginSuccess() {
//        // given
//        SignupRequest signupRequest = SignupRequest.builder()
//                .loginId("testUser")
//                .userName("홍길동")
//                .password("secure123")
//                .build();
//
//        userService.signup(signupRequest);
//
//        // when
//        LoginRequest loginRequest = LoginRequest.builder()
//                .loginId("testUser")
//                .password("secure123")
//                .build();
//
//        User loggedInUser = userService.login(loginRequest);
//
//        // then
//        assertThat(loggedInUser).isNotNull();
//        assertThat(loggedInUser.getLoginId()).isEqualTo("testUser");
//    }


    @Test
    @DisplayName("정상 로그인 시 토큰과 유저 이름 반환")
    void login_success() {
        // given
        LoginRequest loginRequest = LoginRequest.builder()
                .loginId("testuser")
                .password("1234")
                .build();

        // when
        LoginResponse response = userService.login(loginRequest);

        // then
        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(response.getUserName()).isEqualTo("테스트유저");
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시 예외 발생")
    void login_fail_wrong_password() {
        // given
        LoginRequest loginRequest = LoginRequest.builder()
                .loginId("testuser")
                .password("wrong-password")
                .build();

        // when & then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 로그인 시 예외 발생")
    void login_fail_wrong_id() {
        // given
        LoginRequest loginRequest = LoginRequest.builder()
                .loginId("notexist")
                .password("1234")
                .build();

        // when & then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 아이디입니다.");
    }

}
