package com.backend.hamilton.adapter.in.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that a product identifier respects the threshold configured in
 * {@code api.validation.product-id.minimum}.
 *
 * <p>The default message is only a marker: {@code GlobalExceptionHandler} renders the
 * message declared for {@code VALIDATION_FAILED} in the externalised configuration, so
 * no user facing text lives in this annotation.
 */
@Documented
@Constraint(validatedBy = ProductIdValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProductIdConstraint {

    String message() default "product-id";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}