package com.backend.hamilton.adapter.in.web.validation;

import com.backend.hamilton.configuration.properties.ValidationRulesProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

/**
 * Checks a page size against the configured boundaries.
 *
 * <p>The configured minimum of zero is meaningful: it is the sentinel that requests
 * the whole catalog.
 */
@Component
public class PageSizeValidator implements ConstraintValidator<PageSizeConstraint, Integer> {

    private final ValidationRulesProperties rules;

    public PageSizeValidator(ValidationRulesProperties rules) {
        this.rules = rules;
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value >= rules.size().minimum() && value <= rules.size().maximum();
    }
}