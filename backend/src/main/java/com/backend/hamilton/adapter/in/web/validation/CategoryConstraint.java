package com.backend.hamilton.adapter.in.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates the length of a category filter against
 * {@code api.validation.category.min-length} and {@code api.validation.category.max-length}.
 */
@Documented
@Constraint(validatedBy = CategoryValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface CategoryConstraint {

    String message() default "category";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}