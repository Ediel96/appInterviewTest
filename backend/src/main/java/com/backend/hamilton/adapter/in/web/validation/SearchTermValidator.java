package com.backend.hamilton.adapter.in.web.validation;

import com.backend.hamilton.configuration.properties.ValidationRulesProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

/**
 * Checks the length of a free text search term against the configured boundaries.
 */
@Component
public class SearchTermValidator implements ConstraintValidator<SearchTermConstraint, String> {

    private final ValidationRulesProperties rules;

    public SearchTermValidator(ValidationRulesProperties rules) {
        this.rules = rules;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        int length = value.length();
        return length >= rules.search().minLength() && length <= rules.search().maxLength();
    }
}