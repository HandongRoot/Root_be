package com.pard.root.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Value("${root.server.domain}")
    private String domain;

    @Bean
    public OpenAPI openAPI() {
        return (new OpenAPI())
                .components(new Components())
                .info(this.apiInfo())
                .servers(List.of(
                        new Server().url(domain).description("HTTPS 서버")
                ));

    }

    private Info apiInfo() {
        return (new Info())
                .title("Root")
                .description("어디서 많이 저장되어있는데 못찾겠다면 이 서비스가 최고야.")
                .version("0.0.1");
    }
}