package com.backend.hamilton.configuration;

import com.backend.hamilton.configuration.properties.ApiDocsProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Builds the OpenAPI document metadata from the externalised configuration.
 */
@Configuration
public class OpenApiConfiguration {

    /**
     * Creates the OpenAPI descriptor.
     *
     * @param properties API documentation properties bound from {@code api.docs}
     * @return the configured OpenAPI model
     */
    @Bean
    public OpenAPI customOpenAPI(ApiDocsProperties properties) {
        return new OpenAPI()
                .info(new Info()
                        .title(properties.title())
                        .description(properties.description())
                        .version(properties.version()))
                .addServersItem(new Server()
                        .url(properties.serverUrl())
                        .description(properties.serverDescription()));
    }
}