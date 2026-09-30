package com.backend.hamilton.domain.exception;

/**
 * Exception thrown when a product ID is invalid or malformed.
 */
public class InvalidProductIdException extends RuntimeException {

    public InvalidProductIdException(String message) {
        super(message);
    }

    public InvalidProductIdException(String message, Throwable cause) {
        super(message, cause);
    }
}
