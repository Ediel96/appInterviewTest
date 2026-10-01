package com.backend.hamilton.domain.exception;

import java.util.Map;

/**
 * Exception thrown when the external catalog service times out.
 */
public class ExternalServiceTimeoutException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String, String> details;

    public ExternalServiceTimeoutException(String message) {
        super(message);
        this.errorCode = ErrorCode.EXTERNAL_SERVICE_TIMEOUT;
        this.details = Map.of();
    }

    public ExternalServiceTimeoutException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.EXTERNAL_SERVICE_TIMEOUT;
        this.details = Map.of();
    }

    public ExternalServiceTimeoutException(ErrorCode errorCode, Map<String, String> details) {
        super(details.toString());
        this.errorCode = errorCode;
        this.details = Map.copyOf(details);
    }

    public ExternalServiceTimeoutException(ErrorCode errorCode, Map<String, String> details, Throwable cause) {
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