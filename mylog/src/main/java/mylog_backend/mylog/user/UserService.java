package mylog_backend.mylog.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mylog_backend.mylog.auth.LoginRequest;
import mylog_backend.mylog.auth.LoginResponse;
import mylog_backend.mylog.auth.SignupRequest;
import mylog_backend.mylog.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    @Transactional
    public void signup(SignupRequest request) {

        // 중복 로그인 아이디 확인
        if (userRepository.findByLoginId(request.getLoginId()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = request.toUser(encodedPassword);
        userRepository.save(user);

    }

    /**
     * 로그인 요청시 사용
     * @param request : 로그인시 아이디, 비밀번호 입력
     */

    // 단순 로그인 메서드는 JWT발급 로그인 방식으로 대체
//    public User login(LoginRequest request) {
//        User user = userRepository.findByLoginId(request.getLoginId())
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));
//
//        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
//        }
//
//        return user;
//    }


    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtUtil.createToken(user.getLoginId());
        return new LoginResponse(token, user.getUserName());
    }
}
