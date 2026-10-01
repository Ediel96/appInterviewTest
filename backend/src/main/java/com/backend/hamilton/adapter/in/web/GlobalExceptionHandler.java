package com.backend.hamilton.adapter.in.web;

import com.backend.hamilton.adapter.in.web.dto.ApiErrorResponse;
import com.backend.hamilton.configuration.properties.ErrorHandlingProperties;
import com.backend.hamilton.domain.exception.ErrorCode;
import com.backend.hamilton.domain.exception.ExternalServiceException;
import com.backend.hamilton.domain.exception.ExternalServiceTimeoutException;
import com.backend.hamilton.domain.exception.InvalidProductIdException;
import com.backend.hamilton.domain.exception.ProductNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;

/**
 * Global exception handler for API errors.
 *
 * <p>Every response is built from {@code api.errors.definitions}: the HTTP status, the
 * error code and the message all come from the configuration, so behaviour and
 * documentation cannot drift apart. No message or status is hardcoded here.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String UNKNOWN_FIELD = "unknown";

    private final ErrorHandlingProperties errorProperties;

    public GlobalExceptionHandler(ErrorHandlingProperties errorProperties) {
        this.errorProperties = errorProperties;
    }

    @ExceptionHandler(InvalidProductIdException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidProductId(
            InvalidProductIdException ex,
            HttpServletRequest request) {
        return respond(ex.errorCode(), ex.details(), request);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleProductNotFound(
            ProductNotFoundException ex,
            HttpServletRequest request) {
        return respond(ex.errorCode(), ex.details(), request);
    }

    @ExceptionHandler(ExternalServiceTimeoutException.class)
    public ResponseEntity<ApiErrorResponse> handleExternalServiceTimeout(
            ExternalServiceTimeoutException ex,
            HttpServletRequest request) {
        return respond(ex.errorCode(), ex.details(), request);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ApiErrorResponse> handleExternalServiceException(
            ExternalServiceException ex,
            HttpServletRequest request) {
        return respond(ex.errorCode(), ex.details(), request);
    }

    /**
     * Handles constraint violations detected on controller method parameters, which is
     * how the configurable page/size/search/category limits surface.
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodValidation(
            HandlerMethodValidationException ex,
            HttpServletRequest request) {
        String field = ex.getParameterValidationResults().stream()
                .filter(ParameterErrors.class::isInstance)
                .map(ParameterErrors.class::cast)
                .flatMap(errors -> errors.getFieldErrors().stream())
                .map(FieldError::getField)
                .findFirst()
                .orElseGet(() -> ex.getParameterValidationResults().stream()
                        .findFirst()
                        .map(result -> result.getMethodParameter().getParameterName())
                        .orElse(null));
        return respond(ErrorCode.VALIDATION_FAILED, Map.of("field", nullSafe(field)), request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {
        String field = ex.getConstraintViolations().stream()
                .findFirst()
                .map(violation -> lastNode(violation.getPropertyPath().toString()))
                .orElse(null);
        return respond(ErrorCode.VALIDATION_FAILED, Map.of("field", nullSafe(field)), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleBodyValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        FieldError fieldError = ex.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String field = fieldError == null ? null : fieldError.getField();
        return respond(ErrorCode.VALIDATION_FAILED, Map.of("field", nullSafe(field)), request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        return respond(
                ErrorCode.TYPE_MISMATCH,
                Map.of(
                        "field", nullSafe(ex.getName()),
                        "expectedType", ex.getRequiredType() == null ? "unknown" : ex.getRequiredType().getSimpleName()),
                request);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoHandlerFound(
            NoHandlerFoundException ex,
            HttpServletRequest request) {
        return respond(
                ErrorCode.ENDPOINT_NOT_FOUND,
                Map.of("method", nullSafe(ex.getHttpMethod()), "path", nullSafe(ex.getRequestURL())),
                request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception while processing {}", request.getRequestURI(), ex);
        return respond(ErrorCode.UNEXPECTED_ERROR, Map.of(), request);
    }

    private ResponseEntity<ApiErrorResponse> respond(
            ErrorCode code,
            Map<String, String> details,
            HttpServletRequest request) {
        ErrorHandlingProperties.ErrorDefinition definition = errorProperties.resolve(code);
        Map<String, String> placeholders = withRequestDetails(details, request);

        ApiErrorResponse body = new ApiErrorResponse(
                definition.status().value(),
                definition.code(),
                definition.render(placeholders),
                request.getRequestURI(),
                Instant.now().toString());

        return ResponseEntity.status(definition.status()).body(body);
    }

    private Map<String, String> withRequestDetails(Map<String, String> details, HttpServletRequest request) {
        Map<String, String> merged = new TreeMap<>(details);
        merged.putIfAbsent("method", nullSafe(request.getMethod()));
        merged.putIfAbsent("path", nullSafe(request.getRequestURI()));
        return merged;
    }

    private static String lastNode(String propertyPath) {
        int lastDot = propertyPath.lastIndexOf('.');
        return lastDot >= 0 ? propertyPath.substring(lastDot + 1) : propertyPath;
    }

    private static String nullSafe(String value) {
        return value == null || value.isBlank() ? UNKNOWN_FIELD : value;
    }
}