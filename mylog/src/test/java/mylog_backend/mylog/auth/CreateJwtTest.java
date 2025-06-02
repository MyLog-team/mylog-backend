package mylog_backend.mylog.auth;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@Tag("external-integration") // CI에서 빌드시 해당 파일 제외
@SpringBootTest
@ActiveProfiles("test")
class CreateJwtTest {

    @Value("${jwt.secret}")
    private String secretKeyPlain;

    @Test
    void 시크릿키_존재_확인() {
        assertThat(secretKeyPlain).isNotNull();
    }
}
