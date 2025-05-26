package mylog_backend.mylog.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.config.http.SessionCreationPolicy;



@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtProvider, redisTemplate); // 여기에 redisTemplate 넘김
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        // REST API이므로 basic auth 및 csrf 보안을 사용하지 않습니다.
        // Spring Security 6.x에서는 람다 표현식을 권장합니다.
                .httpBasic(httpBasic -> httpBasic.disable())
                .csrf(csrf -> csrf.disable())
                .formLogin(formLogin -> formLogin.disable()) // 폼 로그인은 사용하지 않습니다.
                // JWT를 사용하기 때문에 세션을 사용하지 않습니다.
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // 특정 API에 대해서는 모든 요청을 허가합니다.
                        // requestMatchers에 여러 인자를 전달할 때 varargs를 사용합니다.
                        .requestMatchers(
                                // 회원가입, 로그인 등 인증 없이 허용
                                "/auth/**",
                                // Swagger 관련 경로
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        // 그 외 모든 요청에 대해서는 인증을 필요로 합니다.
                        .anyRequest().authenticated())
                // JWT 인증을 위하여 직접 구현한 필터를 UsernamePasswordAuthenticationFilter 전에 실행
                .addFilterBefore(jwtAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }




}
