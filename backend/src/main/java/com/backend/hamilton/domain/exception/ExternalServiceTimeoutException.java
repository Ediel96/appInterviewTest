package com.backend.hamilton.domain.exception;

/**
 * Exception thrown when an external service call times out.
 */
public class ExternalServiceTimeoutException extends RuntimeException {

    public ExternalServiceTimeoutException(String message) {
        super(message);
    }

    public ExternalServiceTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
