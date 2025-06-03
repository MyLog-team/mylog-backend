package mylog_backend.mylog.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import redis.embedded.RedisServer;

import java.io.IOException;

@TestConfiguration
public class EmbeddedRedisConfig {

    private static final int REDIS_PORT = 6380;
    private RedisServer redisServer;

    @PostConstruct
    public void startRedis() throws IOException {
        redisServer = new RedisServer(REDIS_PORT);
        redisServer.start();
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }


    /**
     * Redis의 포트, 호스트 값을 덮어씌움
     * @param registry
     */
    @DynamicPropertySource
    public static void setRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.port", () -> String.valueOf(REDIS_PORT));
        registry.add("spring.redis.host", () -> "localhost");
    }
}

