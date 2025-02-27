package com.umc.yourun.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server httpsServer = new Server()
                .url("https://yourun.co.kr")  // 실제 도메인으로 변경
                .description("HTTPS Server");

        return new OpenAPI()
                .servers(List.of(httpsServer))  // HTTPS 서버 정보 추가
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .info(new Info()
                        .title("YouRun API Documentation")
                        .version("1.0")
                        .description("YouRun 서비스의 API 문서입니다."))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}