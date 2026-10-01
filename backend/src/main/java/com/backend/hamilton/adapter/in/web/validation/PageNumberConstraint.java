package com.backend.hamilton.adapter.in.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates a page index against {@code api.validation.page.minimum}.
 */
@Documented
@Constraint(validatedBy = PageNumberValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface PageNumberConstraint {

    String message() default "page";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}