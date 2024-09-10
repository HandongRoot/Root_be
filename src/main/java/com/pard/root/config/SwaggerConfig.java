package com.pard.root.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    public SwaggerConfig() {
    }

    @Bean
    public OpenAPI openAPI() {
        return (new OpenAPI()).components(new Components()).info(this.apiInfo());
    }

    private Info apiInfo() {
        return (new Info()).title("Root").description("어디서 많이 저장되어있는데 못찾겠다면 이 서비스가 최고야.").version("0.0.1");
    }
}