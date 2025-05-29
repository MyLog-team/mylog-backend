package mylog_backend.mylog.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;

import static mylog_backend.mylog.auth.JwtConstant.*;

// Util : 어디서든 재사용할 수 있는 공통 로직, 유틸리티 함수가 존재

/** 해당 클래스의 목적
 * 1. JWT 발급 작업 로직
 *
 */
@Component
@Slf4j
//@RequiredArgsConstructor
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JwtUtil {

    private final RedisTemplate<String, String> redisTemplate;

    private final Key key;

    // application.yml에서 secret 값 가져와서 key에 저장
    public JwtUtil(@Value("${jwt.secret}") String secretKey, RedisTemplate<String, String> redisTemplate) {
        log.info("DEBUG: JwtUtil constructor called. Value received for 'jwt.secret': '{}'", secretKey);

        if (secretKey == null || secretKey.isEmpty()) {
            log.error("DEBUG: secretKey is null or empty. Cannot proceed with key decoding. This implies configuration issue.");
            throw new IllegalArgumentException("JWT secret key must not be null or empty from configuration.");
        }

        // 토큰 발급 로직, 예외 발생시 실패한 이유를 출력
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            log.info("DEBUG: Successfully decoded secretKey to bytes. Length: {}", keyBytes.length);

            // JWT 라이브러리 (HS256)는 최소 32바이트 (256비트) 키를 요구합니다.
            if (keyBytes.length < 32) {
                log.error("DEBUG: Decoded key length ({}) is less than 32 bytes. JWT HS256 requires 32 bytes minimum.", keyBytes.length);
                throw new IllegalArgumentException("JWT secret key is too short. Minimum 32 bytes required after Base64 decoding.");
            }

            this.key = Keys.hmacShaKeyFor(keyBytes);
            log.info("DEBUG: JwtUtil successfully initialized with a valid key.");
        } catch (IllegalArgumentException e) {
            log.error("DEBUG: Failed to decode Base64 secretKey or key is invalid. Error: {}", e.getMessage(), e);
            // 이 예외는 보통 Base64 문자열 형식이 잘못되었거나 디코딩 후 길이가 부족할 때 발생합니다.
            throw new RuntimeException("Error initializing JwtUtil: Invalid JWT secret key format or length. Please check application-test.yml 'jwt.secret' value.", e);
        } catch (Exception e) {
            log.error("DEBUG: An unexpected error occurred during JwtUtil initialization: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error during JwtUtil initialization.", e);
        }
        // redis 추가
        this.redisTemplate = redisTemplate; // RedisTemplate 초기화
    }

    // Member 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
    public JWToken generateToken(Long userId) {
        long now = (new Date()).getTime();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);

        String authorities = "ROLE_USER";

        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("auth", authorities)
                .claim("id", String.valueOf(userId))
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JWToken.builder()
                .grantType(GRANT_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            // 1. Redis 블랙리스트에서 토큰이 있는지 확인 (로그아웃된 토큰인지)
            if (redisTemplate.opsForValue().get(token) != null) {
                log.info("블랙리스트에 있는 토큰입니다: {}", token);
                return false; // 블랙리스트에 있으면 유효하지 않음
            }

            // 2. JWT 자체 유효성 검증
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
        }


    // 토큰에서 사용자 ID 추출
    public Long getUserIdFromToken(String token) {
        return Long.parseLong(Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
    }

    // HTTP 요청 헤더에서 토큰 추출
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(JwtConstant.AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtConstant.TOKEN_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 토큰의 남은 유효 시간 계산
    public long getExpiration(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        long now = System.currentTimeMillis();
        return expiration.getTime() - now;
    }


}



//    // 토큰 유효 시간
//    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1시간
//    // JWT 시그니쳐를 만들때 쓰는 비밀 키, 절대 노출하면 안됨!! .env파일로 분리해서 사용
////    private static final String SECRET_KEY_STRING;
//
//    private Key key;
//
//
//    /** @PostConstruct : 스프링에서 의존성 주입이 끝난 후 자동으로 실행되는 초기화 메서드 지정
//     *  JwtUtil 빈이 생성된 후 init() 메서드가 실행됨
//     */
//
//    @PostConstruct
//    public void init() {
//        System.out.println("SecretKey length: " + jwtProperties.getSecretKey().length());
//
//        // 시크릿키를 HMAC-SHA 알고리즘을 이용하여 변환한 값을 key에 할당
//        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
//    }
//
//
//    /**
//     * 토큰 생성 메서드
//     * @param loginId : 사용자의 로그인 아이디
//     * @return
//     */
//    public String createToken(String loginId) {
//        // 생성 시점
//        Date now = new Date();
//        // 만료 시점 = 생성 시점 + 유효시간
//        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);
//
//        // Jwts: JWT(JSON Web Token)을 다루기 위한 라이브러리인 JJWT가 제공하는 도우미 클래스
//        // JWT을 만들고 파싱하는 작업등을 함
//        return Jwts.builder()
//                .setSubject(loginId)
//                .setIssuedAt(now) // 토큰 발급 시간 설정
//                .setExpiration(expiration) // 토큰 만료 시간 설정
//                .signWith(key, SignatureAlgorithm.HS256) // 시그니쳐 제작 알고리즘 사용
//                .compact();
//    }
//
//    /**
//     * JWT에서 로그인 아이디를 꺼내는 메서드
//     * @param token
//     * @return
//     */
//    public String extractLoginId(String token) {
//        return Jwts.parserBuilder() // 파싱
//                .setSigningKey(key) // 서명 검증용 키 설정
//                .build() // 파서 객체 생성
//                .parseClaimsJws(token) // 토큰 파싱, 서명 검증
//                .getBody() // 토큰의 payload(Claims) 가져오기
//                .getSubject(); // subject(=로그인 아이디) 추출
//    }
//
//
//    /**
//     * 토큰이 유효한지 검증하는 메서드
//     * @param token
//     * @return
//     */
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(key) // 서명 키 설정
//                    .build()
//                    .parseClaimsJws(token); // 토큰 파싱 + 서명 검증
//            return true; // 문제 없으면 true 반환
//        } catch (Exception e) { // 예외 발생시 잡아서 false 반환
//            return false;
//        }
//    }
//
//
//    /** HTTP 요청 헤더에서 JWT 토큰을 꺼내는 메서드
//     * @param request
//     * @return
//     */
//    public String resolveToken(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (bearerToken != null && bearerToken.startsWith("Bearer ")) { // 보통 Bearer<토큰> 형식임
//            return bearerToken.substring(7); // "Bearer " 이후 토큰만 추출
//        }
//        return null;
//    }
//
//
//    /** JWT 토큰의 남은 유효 시간을 밀리초 단위로 반환하는 메서드
//     * @param token
//     * @return
//     */
//    public long getExpiration(String token) {
//        Date expiration = Jwts.parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .getExpiration();
//        long now = System.currentTimeMillis();
//        return expiration.getTime() - now;
//    }



