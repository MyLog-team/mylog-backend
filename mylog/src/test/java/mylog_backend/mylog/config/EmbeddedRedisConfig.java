package mylog_backend.mylog.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import redis.embedded.RedisServer;


import java.io.IOException;

@Profile("test") // ⬅️ "test" 프로파일이 활성화될 때만 이 설정을 사용
@TestConfiguration   // ⬅️ @TestConfiguration 대신 @Configuration 을 사용
public class EmbeddedRedisConfig {

    private static final int REDIS_PORT = 6380;
    private RedisServer redisServer;

    @PostConstruct
    public void startRedis() throws IOException {
        // ✅ PostConstruct 메소드가 실행되는지 확인하는 로그
        System.out.println("✅✅✅ [EmbeddedRedisConfig] @PostConstruct: startRedis() - STARTING EMBEDDED REDIS ON PORT " + REDIS_PORT);

        try {
            redisServer = RedisServer.newRedisServer()
                            .port(REDIS_PORT)
                            .slaveOf("localhost", REDIS_PORT)
                            .setting("maxmemoy 128M")
                            .build();

            redisServer.start();
            System.out.println("✅✅✅ [EmbeddedRedisConfig] @PostConstruct: startRedis() - EMBEDDED REDIS STARTED SUCCESSFULLY");
        } catch (Exception e) {
            // ✅ 혹시 서버 시작 시 에러가 발생하면 로그로 출력
            System.err.println("🔥🔥🔥 [EmbeddedRedisConfig] FAILED TO START EMBEDDED REDIS: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void stopRedis() throws IOException {
        // ✅ PreDestroy 메소드가 실행되는지 확인하는 로그
        System.out.println("✅✅✅ [EmbeddedRedisConfig] @PreDestroy: stopRedis()");
        if (redisServer != null) {
            redisServer.stop();
        }
    }

    @DynamicPropertySource
    public static void setRedisProperties(DynamicPropertyRegistry registry) {
        // ✅ DynamicPropertySource가 실행되는지 확인하는 로그
        System.out.println("✅✅✅ [EmbeddedRedisConfig] @DynamicPropertySource: Setting redis properties to port " + REDIS_PORT);
        registry.add("redis.port", () -> String.valueOf(REDIS_PORT));
        registry.add("redis.host", () -> "localhost");
    }
}

