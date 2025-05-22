package mylog_backend.mylog.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mylog_backend.mylog.user.UserService;
import mylog_backend.mylog.util.JwtUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final JwtService jwtService;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 회원가입
     * @param request : 회원가입 요청
     * @return : 성공 확인
     */
    @PostMapping("/auth/signup")
    private ResponseEntity<Void> singup(@RequestBody @Valid SignupRequest request) {
        userService.signup(request);
        return ResponseEntity.ok().build();
    }


    /**
     * 로그인 메서드
     * @param request
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid LoginRequest request) {
        userService.login(request);
        return ResponseEntity.ok().build();
    }


    /**
     * 로그아웃 메서드
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // 헤더에서 토큰을 꺼냄
        String token = jwtUtil.resolveToken(request);
        if (token == null) { // 토큰이 없으면 400 상태 코드 반환
            return ResponseEntity.badRequest().body("토큰이 없습니다.");
        }
        if (redisTemplate.hasKey(token)) {  // redis에 토큰이 있으면 사용불가
            // Redis 서버에 key가 존재하는지 boolean형으로 판단
            throw new SecurityException("이미 로그아웃된 토큰입니다.");
        }
        jwtService.logout(token);
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }



}
