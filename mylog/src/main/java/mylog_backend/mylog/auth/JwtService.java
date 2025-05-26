package mylog_backend.mylog.auth;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import mylog_backend.mylog.common.exception.BadCredentialsException;
import mylog_backend.mylog.common.exception.DuplicateLoginIdException;
import mylog_backend.mylog.common.exception.UserNotFoundException;
import mylog_backend.mylog.user.User;
import mylog_backend.mylog.user.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    /**
     * 회원가입
     * @param request
     */
    @Transactional
    public void signup(SignupRequest request) {
        // 중복 로그인 아이디 확인
        if (userRepository.findByLoginId(request.getLoginId()).isPresent()) {
            throw new DuplicateLoginIdException("이미 사용 중인 아이디입니다: " + request.getLoginId());
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = request.toUser(encodedPassword);
        userRepository.save(user);
    }


    /**
     * 로그인
     * @param request
     * @return
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 아이디입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        String authorities = "ROLE_USER";

        // JWT 토큰 생성 (액세스 토큰 + 리프레시 토큰)
        JWToken token = jwtUtil.generateToken(user.getId());

        return LoginResponse.builder()
                .grantType(token.getGrantType())
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .userName(user.getUserName())
                .build();
    }

    /**
     * 로그아웃 메서드
     * @param token : 반납할 토큰
     */
    public void logout(String token) {
        if (jwtUtil.validateToken(token)) { // 유효한 토큰일때
            long expiration = jwtUtil.getExpiration(token); //유효기간을 연산해서 할당
            // JWT 자체는 무상태라서 서버에 토큰 정보가 담기지 않는다.
            // 따라서 강제로 토큰을 만료하는게 불가능하기에 따로 관리해야하는데..
            // 이때 Redis에 토큰 정보를 담아둔다!

            // Redis에 로그아웃되어 만료돤 토큰을 넣어두고, 사용불가한 토큰을 관리할 수 있다.
            redisTemplate.opsForValue().set(token, "logout", expiration, TimeUnit.MILLISECONDS);
        }
    }
}
