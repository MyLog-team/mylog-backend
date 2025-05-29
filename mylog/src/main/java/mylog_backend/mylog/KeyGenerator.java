package mylog_backend.mylog;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) {
        // HS256 알고리즘에 적합한 256비트(32바이트) 이상의 강력한 키를 생성하고 Base64로 인코딩합니다.
        String base64Key = Base64.getEncoder().encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded());
        System.out.println("새로운 Base64 인코딩된 JWT_SECRET_KEY:");
        System.out.println(base64Key);
        System.out.println("\n이 키를 .env 파일의 JWT_SECRET_KEY 값으로 사용하세요.");
    }
}