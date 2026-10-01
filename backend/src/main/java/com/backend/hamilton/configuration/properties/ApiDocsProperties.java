package com.backend.hamilton.configuration.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Metadata and routing information of the public API, bound from {@code api.docs}.
 *
 * @param title OpenAPI info title
 * @param description OpenAPI info description
 * @param version OpenAPI info version
 * @param serverUrl URL advertised in the OpenAPI server entry
 * @param serverDescription description of the OpenAPI server entry
 * @param endpoints paths exposed by the HTTP inbound adapter
 */
@ConfigurationProperties(prefix = "api.docs")
@Validated
public record ApiDocsProperties(
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String version,
        @NotBlank @Pattern(regexp = "^https?://.+", message = "must be an absolute http(s) URL") String serverUrl,
        @NotBlank String serverDescription,
        @Valid @NotNull Endpoints endpoints
) {

    /**
     * Paths of the endpoints owned by this service.
     *
     * @param productsBasePath base path of the product collection
     * @param productIdPath path of a single product, relative to the base path
     */
    public record Endpoints(
            @NotBlank @Pattern(regexp = "^/.*", message = "must start with /") String productsBasePath,
            @NotBlank @Pattern(regexp = "^/.*", message = "must start with /") String productIdPath
    ) {
    }

    /**
     * Full path of a single product.
     *
     * @return base path concatenated with the id path
     */
    public String productByIdPath() {
        return endpoints.productsBasePath() + endpoints.productIdPath();
    }
}