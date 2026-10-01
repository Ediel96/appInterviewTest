package com.backend.hamilton.domain.exception;

/**
 * Stable error codes used across the application.
 *
 * <p>These codes are returned in the error response ({@code ApiErrorResponse})
 * and act as the lookup key against {@code api.errors.definitions} in the
 * externalised configuration.
 */
public enum ErrorCode {
    INVALID_PRODUCT_ID("invalid-product-id"),
    PRODUCT_NOT_FOUND("product-not-found"),
    EXTERNAL_SERVICE_ERROR("external-service-error"),
    EXTERNAL_SERVICE_TIMEOUT("external-service-timeout"),
    VALIDATION_FAILED("validation-failed"),
    TYPE_MISMATCH("type-mismatch"),
    ENDPOINT_NOT_FOUND("endpoint-not-found"),
    UNEXPECTED_ERROR("unexpected-error");

    private final String configKey;

    ErrorCode(String configKey) {
        this.configKey = configKey;
    }

    /**
     * @return the key used to resolve the definition in {@code application.yml}
     */
    public String configKey() {
        return configKey;
    }
}