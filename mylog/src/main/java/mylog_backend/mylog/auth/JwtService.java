package mylog_backend.mylog.auth;

import lombok.RequiredArgsConstructor;
import mylog_backend.mylog.util.JwtUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;

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
