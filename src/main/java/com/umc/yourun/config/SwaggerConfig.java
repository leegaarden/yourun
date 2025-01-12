package com.umc.yourun.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("YouRun API Documentation")
                        .version("1.0")
                        .description("YouRun 서비스의 API 문서입니다.")
                        .contact(new Contact()
                                .name("YouRun")
                                .email("yourun@gmail.com")));
    }
}