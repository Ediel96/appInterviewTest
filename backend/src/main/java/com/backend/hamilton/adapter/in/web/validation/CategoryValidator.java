package com.backend.hamilton.adapter.in.web.validation;

import com.backend.hamilton.configuration.properties.ValidationRulesProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

/**
 * Checks the length of a category filter against the configured boundaries.
 */
@Component
public class CategoryValidator implements ConstraintValidator<CategoryConstraint, String> {

    private final ValidationRulesProperties rules;

    public CategoryValidator(ValidationRulesProperties rules) {
        this.rules = rules;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        int length = value.length();
        return length >= rules.category().minLength() && length <= rules.category().maxLength();
    }
}