package mylog_backend.mylog.videoRecommend;

import lombok.Builder;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "youtube")
public class YoutubeProperties {
    private String apiKey;

    // 반드시 setter가 필요함! (스프링이 프로퍼티 주입에 사용)
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

}
