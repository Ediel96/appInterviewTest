package com.backend.hamilton.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI documentation configuration.
 */
@Configuration
public class OpenApiConfiguration {

    @Value("${openapi.title}")
    private String title;

    @Value("${openapi.description}")
    private String description;

    @Value("${openapi.version}")
    private String version;

    @Value("${openapi.server-url}")
    private String serverUrl;

    @Value("${openapi.server-description}")
    private String serverDescription;

    /**
     * Configures the OpenAPI documentation bean.
     *
     * @return configured OpenAPI instance
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .description(description)
                        .version(version))
                .addServersItem(new Server()
                        .url(serverUrl)
                        .description(serverDescription));
    }
}
