package com.backend.hamilton.domain.exception;

import java.util.Map;

/**
 * Exception thrown when a requested product does not exist.
 */
public class ProductNotFoundException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String, String> details;

    public ProductNotFoundException(String message) {
        super(message);
        this.errorCode = ErrorCode.PRODUCT_NOT_FOUND;
        this.details = Map.of();
    }

    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.PRODUCT_NOT_FOUND;
        this.details = Map.of();
    }

    public ProductNotFoundException(ErrorCode errorCode, Map<String, String> details) {
        super(details.toString());
        this.errorCode = errorCode;
        this.details = Map.copyOf(details);
    }

    public ProductNotFoundException(ErrorCode errorCode, Map<String, String> details, Throwable cause) {
        super(details.toString(), cause);
        this.errorCode = errorCode;
        this.details = Map.copyOf(details);
    }

    public ErrorCode errorCode() {
        return errorCode;
    }

    public Map<String, String> details() {
        return details;
    }
}