package com.backend.hamilton.domain.exception;

import java.util.Map;

/**
 * Exception thrown when a product ID is invalid or malformed.
 */
public class InvalidProductIdException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String, String> details;

    public InvalidProductIdException(String message) {
        super(message);
        this.errorCode = ErrorCode.INVALID_PRODUCT_ID;
        this.details = Map.of();
    }

    public InvalidProductIdException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.INVALID_PRODUCT_ID;
        this.details = Map.of();
    }

    public InvalidProductIdException(ErrorCode errorCode, Map<String, String> details) {
        super(details.toString());
        this.errorCode = errorCode;
        this.details = Map.copyOf(details);
    }

    public InvalidProductIdException(ErrorCode errorCode, Map<String, String> details, Throwable cause) {
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