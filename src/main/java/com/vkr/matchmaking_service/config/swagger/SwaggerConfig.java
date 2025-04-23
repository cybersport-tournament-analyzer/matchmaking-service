package com.vkr.matchmaking_service.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://77.221.158.197:8081")
                                .description("Test server"),
                        new Server().url("http://localhost:8081")
                                .description("Localhost server")
                ));
    }
}
