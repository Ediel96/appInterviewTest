package com.backend.hamilton.adapter.in.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates a page size against {@code api.validation.size.minimum} and
 * {@code api.validation.size.maximum}.
 */
@Documented
@Constraint(validatedBy = PageSizeValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface PageSizeConstraint {

    String message() default "size";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}