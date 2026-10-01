package com.backend.hamilton.domain.exception;

import java.util.Map;

/**
 * Exception thrown when the external catalog service returns an error or an
 * unusable payload.
 */
public class ExternalServiceException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String, String> details;

    public ExternalServiceException(String message) {
        super(message);
        this.errorCode = ErrorCode.EXTERNAL_SERVICE_ERROR;
        this.details = Map.of();
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.EXTERNAL_SERVICE_ERROR;
        this.details = Map.of();
    }

    public ExternalServiceException(ErrorCode errorCode, Map<String, String> details) {
        super(details.toString());
        this.errorCode = errorCode;
        this.details = Map.copyOf(details);
    }

    public ExternalServiceException(ErrorCode errorCode, Map<String, String> details, Throwable cause) {
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