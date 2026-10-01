package com.backend.hamilton.adapter.in.web.validation;

import com.backend.hamilton.configuration.properties.ValidationRulesProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that the constraint validators take their boundaries from the configuration
 * rather than from values written in code.
 */
class ConstraintValidatorsTest {

    @Test
    void productIdUsesConfiguredMinimum() {
        ProductIdValidator validator = new ProductIdValidator(rules(5, 0, 0, 20, 2, 50, 1, 30));

        assertTrue(validator.isValid(5L, null));
        assertTrue(validator.isValid(6L, null));
        assertFalse(validator.isValid(4L, null));
        assertFalse(validator.isValid(0L, null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void productIdHonoursADifferentConfiguredMinimum() {
        ProductIdValidator validator = new ProductIdValidator(rules(1, 0, 0, 20, 2, 50, 1, 30));

        assertTrue(validator.isValid(1L, null));
        assertFalse(validator.isValid(0L, null));
    }

    @Test
    void pageNumberUsesConfiguredMinimum() {
        PageNumberValidator validator = new PageNumberValidator(rules(1, 3, 0, 20, 2, 50, 1, 30));

        assertTrue(validator.isValid(3, null));
        assertFalse(validator.isValid(2, null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void pageSizeUsesConfiguredBounds() {
        PageSizeValidator validator = new PageSizeValidator(rules(1, 0, 0, 100, 2, 50, 1, 30));

        assertTrue(validator.isValid(0, null));
        assertTrue(validator.isValid(1, null));
        assertTrue(validator.isValid(100, null));
        assertFalse(validator.isValid(101, null));
        assertFalse(validator.isValid(-1, null));
    }

    @Test
    void pageSizeHonoursADifferentConfiguredRange() {
        PageSizeValidator validator = new PageSizeValidator(rules(1, 0, 2, 7, 2, 50, 1, 30));

        assertTrue(validator.isValid(2, null));
        assertTrue(validator.isValid(7, null));
        assertFalse(validator.isValid(1, null));
        assertFalse(validator.isValid(8, null));
    }

    @Test
    void searchTermUsesConfiguredLengths() {
        SearchTermValidator validator = new SearchTermValidator(rules(1, 0, 0, 100, 3, 10, 1, 30));

        assertTrue(validator.isValid("abc", null));
        assertTrue(validator.isValid("abcdefghij", null));
        assertFalse(validator.isValid("ab", null));
        assertFalse(validator.isValid("abcdefghijk", null));
        assertTrue(validator.isValid(null, null));
        assertTrue(validator.isValid("  ", null));
    }

    @Test
    void categoryUsesConfiguredLengths() {
        CategoryValidator validator = new CategoryValidator(rules(1, 0, 0, 100, 2, 50, 3, 6));

        assertTrue(validator.isValid("beauty", null));
        assertFalse(validator.isValid("ab", null));
        assertFalse(validator.isValid("toolong", null));
        assertTrue(validator.isValid(null, null));
    }

    private static ValidationRulesProperties rules(
            int idMinimum,
            int pageMinimum,
            int sizeMinimum,
            int sizeMaximum,
            int searchMinLength,
            int searchMaxLength,
            int categoryMinLength,
            int categoryMaxLength) {
        return new ValidationRulesProperties(
                new ValidationRulesProperties.ProductIdRule((long) idMinimum),
                new ValidationRulesProperties.PageRule(pageMinimum, 0),
                new ValidationRulesProperties.SizeRule(sizeMinimum, sizeMaximum, 0),
                new ValidationRulesProperties.TextRule(searchMinLength, searchMaxLength),
                new ValidationRulesProperties.TextRule(categoryMinLength, categoryMaxLength));
    }
}