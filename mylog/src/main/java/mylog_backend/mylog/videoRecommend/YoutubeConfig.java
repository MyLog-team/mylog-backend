package mylog_backend.mylog.videoRecommend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class YoutubeConfig {

    @Value("${youtube.api-key}")
    private String apiKey;

    @Bean
    public YoutubeClient youtubeClient() {
        return new YoutubeClient(apiKey);
    }
}
