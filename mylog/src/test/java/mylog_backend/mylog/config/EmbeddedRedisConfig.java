package mylog_backend.mylog.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import redis.embedded.RedisServer;


import java.io.IOException;


@Profile("test") // application-test.yml의 설정 이용!
@TestConfiguration   // ⬅️ @TestConfiguration 대신 @Configuration 을 사용
public class EmbeddedRedisConfig {

    private static final int REDIS_PORT = 6380;
    private RedisServer redisServer;

    @PostConstruct
    public void startRedis() throws IOException {
        // ✅ PostConstruct 메소드가 실행되는지 확인하는 로그
        System.out.println("✅✅✅ [EmbeddedRedisConfig] @PostConstruct: startRedis() - Port에서 임베디드 레디스 실행 시작.. " + REDIS_PORT);

        try {
            redisServer = RedisServer.newRedisServer()
                            .port(REDIS_PORT)
                            .setting("maxmemory 128M")
                            .build();

            redisServer.start();
            System.out.println("✅✅✅ [EmbeddedRedisConfig] @PostConstruct: startRedis() - 임베디드 레디스 실행 성공!");
        } catch (Exception e) {
            // ✅ 혹시 서버 시작 시 에러가 발생하면 로그로 출력
            System.err.println("🔥🔥🔥 [EmbeddedRedisConfig] @PostConstruct: startRedis() - 임베디드 레디스 실행 실패...: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void stopRedis() throws IOException {
        // ✅ PreDestroy 메소드가 실행되는지 확인하는 로그
        System.out.println("✅✅✅ [EmbeddedRedisConfig] @PreDestroy: stopRedis(), 임베디드 레디스 서버 정지");
        if (redisServer != null) {
            redisServer.stop();
        }
    }

    @DynamicPropertySource
    public static void setRedisProperties(DynamicPropertyRegistry registry) {
        // ✅ DynamicPropertySource가 실행되는지 확인하는 로그
        System.out.println("✅✅✅ [EmbeddedRedisConfig] @DynamicPropertySource: 레디스 포트 및 설정 확인 " + REDIS_PORT);
        registry.add("redis.port", () -> String.valueOf(REDIS_PORT));
        registry.add("redis.host", () -> "127.0.0.1");
    }
}

