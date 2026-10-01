package com.backend.hamilton.adapter.in.web.validation;

import com.backend.hamilton.configuration.properties.ValidationRulesProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

/**
 * Checks a page index against the configured minimum.
 */
@Component
public class PageNumberValidator implements ConstraintValidator<PageNumberConstraint, Integer> {

    private final ValidationRulesProperties rules;

    public PageNumberValidator(ValidationRulesProperties rules) {
        this.rules = rules;
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value >= rules.page().minimum();
    }
}