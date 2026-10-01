package com.backend.hamilton.adapter.in.web.validation;

import com.backend.hamilton.configuration.properties.ValidationRulesProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

/**
 * Checks a product identifier against the configured minimum.
 */
@Component
public class ProductIdValidator implements ConstraintValidator<ProductIdConstraint, Long> {

    private final ValidationRulesProperties rules;

    public ProductIdValidator(ValidationRulesProperties rules) {
        this.rules = rules;
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value >= rules.productId().minimum();
    }
}