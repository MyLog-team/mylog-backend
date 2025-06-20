package mylog_backend.mylog.auth;

import jakarta.annotation.PostConstruct;
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

    private final RedisTemplate<String, String> redisTemplate; // spring data redis
    private final JwtUtil jwtUtil;
    private final JwtProvider jwtProvider;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    // ================== 디버깅 코드 추가 시작 ==================
    @PostConstruct
    public void checkDependencies() {
        if (jwtProvider == null) {
            log.error("!!!!!! FATAL: JwtService 생성 후 jwtProvider가 null입니다. 의존성 주입에 실패했습니다. !!!!!!");
        } else {
            log.info("--- SUCCESS: JwtService 생성 및 jwtProvider 주입 성공. jwtProvider의 클래스: {}", jwtProvider.getClass().getName());
        }
    }
    // ================== 디버깅 코드 추가 끝 ====================




    /**
     * 회원가입
     * @param request : 회원가입 요청 DTO, 사용자 정보가 들어감
     * @return : 회원가입 성공 메시지와 저장된 유저의 아이디
     */
    @Transactional
    public SignupResponse signup(SignupRequest request) {

        // 중복 로그인 아이디 확인
        if (userRepository.findByLoginId(request.getLoginId()).isPresent()) {
            throw new DuplicateLoginIdException("이미 사용 중인 아이디입니다: " + request.getLoginId());
        }

        // 사용자를 DB에 저장
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = request.toUser(encodedPassword);

        // DB에 저장된 사용자
        User savedUser =  userRepository.save(user);

        return SignupResponse.of("회원가입되었습니다.", savedUser.getId());
    }


    /**
     * 로그인
     * @param request : 로그인 요청 DTO, 로그인 아이디와 패스워드 필요
     * @return : 로그인 응답 DTO
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        // 존재하는 유저인지 판단
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 아이디입니다."));

        // 비밀번호가 맞는지 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }


        // 사용자 아이디로 JWT 액세스/리프레시 토큰 생성
        JWToken token = jwtUtil.generateToken(user.getId());

        return LoginResponse.from("로그인에 성공하였습니다.", token.getAccessToken(), token.getRefreshToken(),  token.getGrantType());
    }


    /**
     * 로그아웃 메서드
     * @param token : 반납할 토큰
     */
    public void logout(String token) {
        if (jwtProvider.validateToken(token)) { // 유효한 토큰일때
            long expiration = jwtUtil.getExpiration(token); //유효기간을 연산해서 할당
            // JWT 자체는 무상태라서 서버에 토큰 정보가 담기지 않는다.
            // 따라서 강제로 토큰을 만료하는게 불가능하기에 따로 관리해야하는데..
            // 이때 Redis에 토큰 정보를 담아둔다!

            // Redis에 로그아웃되어 만료돤 토큰을 넣어두고, 사용불가한 토큰을 관리할 수 있다.
            // 3. Redis에 토큰 저장 (블랙리스트 추가)
            try {
                redisTemplate.opsForValue().set(token, "logout", expiration, TimeUnit.MILLISECONDS);
                log.info("Access Token이 Redis 블랙리스트에 성공적으로 추가되었습니다. 토큰: {} 남은 유효기간: {}ms", token, expiration);
            } catch (Exception e) {
                // Redis 연결 오류, Redis 서버 문제 등
                log.error("Redis에 토큰 저장 중 오류 발생. 토큰: {}, 오류: {}", token, e.getMessage(), e);
                // Redis 오류는 500 Internal Server Error로 이어질 수 있으므로,
                // 적절한 사용자 정의 예외로 래핑하여 컨트롤러로 전달
                throw new RuntimeException("로그아웃 처리 중 데이터베이스 오류가 발생했습니다.", e);
            }
            // token(eyJ...) : "logout" 형태로 저장
        }
    }
}
