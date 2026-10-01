package com.backend.hamilton.configuration.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.Optional;

/**
 * Configuration for the DummyJSON client, bound from {@code clients.dummy-json}.
 */
@ConfigurationProperties(prefix = "clients.dummy-json")
@Validated
public record DummyJsonClientProperties(
        @NotBlank String baseUrl,
        @Valid @NotNull Paths paths,
        @Valid @NotNull Query query,
        @Valid @NotNull Timeouts timeouts
) {

    /**
     * API paths.
     */
    public record Paths(
            @NotBlank String products,
            @NotBlank String productById
    ) {
    }

    /**
     * Default query parameters applied by the adapter.
     */
    public record Query(
            @NotNull Integer allProductsLimit
    ) {
    }

    /**
     * HTTP timeouts.
     */
    public record Timeouts(
            @NotNull @DurationMin(message = "connect timeout must be positive", millis = 1) Duration connect,
            @NotNull @DurationMin(message = "read timeout must be positive", millis = 1) Duration read
    ) {
    }

    /**
     * Returns the absolute URI to list all products.
     */
    public String allProductsUri() {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .path(paths.products())
                .queryParam("limit", query.allProductsLimit())
                .toUriString();
    }

    /**
     * Returns the absolute URI template-compatible path for a product by its id.
     * The adapter still uses {@link org.springframework.web.client.RestClient#get()
     * .uri(String uriTemplate, Object... uriVariables)} to expand {@code {id}}.
     */
    public String productByIdTemplate() {
        return paths.productById();
    }
}