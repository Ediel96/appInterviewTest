package com.backend.hamilton.configuration.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Validation boundaries applied to the API inputs, bound from {@code api.validation}.
 *
 * <p>These limits are the single source of truth for both the runtime Bean Validation
 * constraints and the boundaries published in the OpenAPI schema.
 *
 * @param productId boundaries of the product identifier
 * @param page boundaries of the page number
 * @param size boundaries of the page size, where the minimum means "no limit"
 * @param search boundaries of the free text search term
 * @param category boundaries of the category filter
 */
@ConfigurationProperties(prefix = "api.validation")
@Validated
public record ValidationRulesProperties(
        @Valid @NotNull ProductIdRule productId,
        @Valid @NotNull PageRule page,
        @Valid @NotNull SizeRule size,
        @Valid @NotNull TextRule search,
        @Valid @NotNull TextRule category
) {

    /**
     * @param minimum lowest accepted identifier; also the value enforced by the domain
     */
    public record ProductIdRule(@NotNull @Positive Long minimum) {
    }

    /**
     * @param minimum lowest accepted page index
     * @param defaultValue page applied when the client does not send one
     */
    public record PageRule(@NotNull @PositiveOrZero Integer minimum,
                           @NotNull @PositiveOrZero Integer defaultValue) {
    }

    /**
     * @param minimum lowest accepted page size; configured as zero to mean "no limit"
     * @param maximum highest accepted page size
     * @param defaultValue page size applied when the client does not send one
     */
    public record SizeRule(@NotNull @PositiveOrZero Integer minimum,
                           @NotNull @Positive Integer maximum,
                           @NotNull @PositiveOrZero Integer defaultValue) {
    }

    /**
     * @param minLength shortest accepted value
     * @param maxLength longest accepted value
     */
    public record TextRule(@NotNull @PositiveOrZero Integer minLength,
                           @NotNull @Positive Integer maxLength) {
    }

    /**
     * Resolves the page size to apply, falling back to the configured default.
     *
     * @param requested value sent by the client, may be null
     * @return the requested value or the configured default
     */
    public int resolveSize(Integer requested) {
        return requested == null ? size.defaultValue() : requested;
    }

    /**
     * Resolves the page to apply, falling back to the configured default.
     *
     * @param requested value sent by the client, may be null
     * @return the requested value or the configured default
     */
    public int resolvePage(Integer requested) {
        return requested == null ? page.defaultValue() : requested;
    }
}