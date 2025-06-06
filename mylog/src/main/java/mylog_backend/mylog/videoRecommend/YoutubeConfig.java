package mylog_backend.mylog.videoRecommend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 외부 API와 HTTP 통신을 위해 외부Client, 외부Config가 반드시 필요하다.
@Configuration
public class YoutubeConfig { // YoutubeClient 요청을 보내기위해 환경변수를 주입하는 역할

    // env 파일에서 youtube api 키를 주입받음
    @Value("${youtube.api-key}")
    private String apiKey;

    // YoutubeClient에 api 키를 주입해줌
    @Bean
    public YoutubeClient youtubeClient() {
        return new YoutubeClient(apiKey);
    }
}
