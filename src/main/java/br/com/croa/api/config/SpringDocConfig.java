package br.com.croa.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;

import static io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP;
import static java.util.stream.Collectors.toList;

@Configuration
@RequiredArgsConstructor
public class SpringDocConfig {

    @Value("${springdoc.swagger-ui.server.list}")
    private List<String> servers;

    private final Environment environment;

    @Bean
    public OpenAPI customOpenAPI() {
        final var info = new OpenAPI()
                .servers(servers.stream().map(s -> new Server().url(s)).collect(toList()))
                .info(new Info().title("Croa Api")
                        .description("Croa Api application")
                        .version("1.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
        if (Arrays.stream(environment.getActiveProfiles())
                .parallel().anyMatch(s -> s.equalsIgnoreCase("local") ||
                        s.equalsIgnoreCase("heroku") ||
                        s.equalsIgnoreCase("test"))) {
            return info;
        }
        return info
                .components(new Components().addSecuritySchemes("bearerAuth", new SecurityScheme().type(HTTP).scheme("bearer").bearerFormat("JWT")));
    }
}
