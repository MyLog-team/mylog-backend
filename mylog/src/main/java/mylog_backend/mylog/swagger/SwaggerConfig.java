package mylog_backend.mylog.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    // JWT 보안 스킴의 이름을 상수로 정의합니다.
    private static final String JWT_SECURITY_SCHEME_NAME = "bearerAuth"; // 일반적으로 "bearerAuth"를 사용합니다.

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // API 전반에 대한 정보를 설정합니다.
                .info(new Info()
                        .title("MyLog API Test") // API의 제목
                        .description("MyLog 프로젝트 Swagger UI 연습") // API에 대한 설명
                        .version("1.0.0")) // API의 버전
                // JWT 보안 스킴을 컴포넌트에 추가합니다.
                // 이 스킴은 API 요청에 대한 인증을 정의하며, 스웨거 UI에서 'Authorize' 버튼을 통해 사용됩니다.
                .components(new Components()
                        .addSecuritySchemes(JWT_SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(JWT_SECURITY_SCHEME_NAME) // 스킴의 이름 (Swagger UI에 표시될 이름)
                                .type(SecurityScheme.Type.HTTP) // HTTP 기반 인증
                                .scheme("bearer") // 인증 스킴 (Bearer Token)
                                .bearerFormat("JWT"))) // 토큰 형식
                // 모든 API 엔드포인트에 이 JWT 보안 스킴을 적용하도록 설정합니다.
                // 이는 스웨거 UI에서 'Authorize' 버튼이 활성화되고,
                // 이 스킴을 통해 전송되는 요청에 JWT 토큰이 포함되도록 합니다.
                // 이 설정은 API 정의 자체의 접근과는 무관합니다.
                .addSecurityItem(new SecurityRequirement().addList(JWT_SECURITY_SCHEME_NAME));
    }
}


//                .info(new Info()
//                        .title("MyLog API")
//                        .version("1.0.0")
//                        .description("MyLog 프로젝트 Swagger 문서"))
//                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
//                .components(new io.swagger.v3.oas.models.Components()
//                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
//                                .name(SECURITY_SCHEME_NAME)
//                                .type(Type.HTTP)
//                                .scheme("bearer")
//                                .bearerFormat("JWT")));
//    }
//}
