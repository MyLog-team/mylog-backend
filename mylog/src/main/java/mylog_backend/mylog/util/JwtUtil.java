package mylog_backend.mylog.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import mylog_backend.mylog.auth.JwtProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

// Util : 어디서든 재사용할 수 있는 공통 로직, 유틸리티 함수가 존재
@Component
@RequiredArgsConstructor
public class JwtUtil { // JWT 관련 반복 작업들을 정의

    private final JwtProperties jwtProperties;

    // 토큰 유효 시간
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1시간
    // JWT 시그니쳐를 만들때 쓰는 비밀 키, 절대 노출하면 안됨!! .env파일로 분리해서 사용
//    private static final String SECRET_KEY_STRING;

    private Key key;


    /** @PostConstruct : 스프링에서 의존성 주입이 끝난 후 자동으로 실행되는 초기화 메서드 지정
     *  JwtUtil 빈이 생성된 후 init() 메서드가 실행됨
     */

    @PostConstruct
    public void init() {
        System.out.println("SecretKey length: " + jwtProperties.getSecretKey().length());

        // 시크릿키를 HMAC-SHA 알고리즘을 이용하여 변환한 값을 key에 할당
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
    }


    /**
     * 토큰 생성 메서드
     * @param loginId : 사용자의 로그인 아이디
     * @return
     */
    public String createToken(String loginId) {
        // 생성 시점
        Date now = new Date();
        // 만료 시점 = 생성 시점 + 유효시간
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);

        // Jwts: JWT(JSON Web Token)을 다루기 위한 라이브러리인 JJWT가 제공하는 도우미 클래스
        // JWT을 만들고 파싱하는 작업등을 함
        return Jwts.builder()
                .setSubject(loginId)
                .setIssuedAt(now) // 토큰 발급 시간 설정
                .setExpiration(expiration) // 토큰 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS256) // 시그니쳐 제작 알고리즘 사용
                .compact();
    }

    /**
     * JWT에서 로그인 아이디를 꺼내는 메서드
     * @param token
     * @return
     */
    public String extractLoginId(String token) {
        return Jwts.parserBuilder() // 파싱
                .setSigningKey(key) // 서명 검증용 키 설정
                .build() // 파서 객체 생성
                .parseClaimsJws(token) // 토큰 파싱, 서명 검증
                .getBody() // 토큰의 payload(Claims) 가져오기
                .getSubject(); // subject(=로그인 아이디) 추출
    }


    /**
     * 토큰이 유효한지 검증하는 메서드
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key) // 서명 키 설정
                    .build()
                    .parseClaimsJws(token); // 토큰 파싱 + 서명 검증
            return true; // 문제 없으면 true 반환
        } catch (Exception e) { // 예외 발생시 잡아서 false 반환
            return false;
        }
    }


    /** HTTP 요청 헤더에서 JWT 토큰을 꺼내는 메서드
     * @param request
     * @return
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) { // 보통 Bearer<토큰> 형식임
            return bearerToken.substring(7); // "Bearer " 이후 토큰만 추출
        }
        return null;
    }


    /** JWT 토큰의 남은 유효 시간을 밀리초 단위로 반환하는 메서드
     * @param token
     * @return
     */
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

