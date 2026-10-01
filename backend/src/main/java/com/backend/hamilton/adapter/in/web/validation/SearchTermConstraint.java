package com.backend.hamilton.adapter.in.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates the length of a free text search term against
 * {@code api.validation.search.min-length} and {@code api.validation.search.max-length}.
 *
 * <p>Null and blank values are accepted: they mean "filter not applied".
 */
@Documented
@Constraint(validatedBy = SearchTermValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface SearchTermConstraint {

    String message() default "search";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}