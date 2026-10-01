package com.backend.hamilton.configuration.properties;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that the validation boundaries are bound from configuration and that
 * invalid values make the context fail to start.
 */
class ValidationRulesPropertiesBindingTest {

    private static final String[] VALID_RULES = {
            "api.validation.product-id.minimum=2",
            "api.validation.page.minimum=1",
            "api.validation.page.default-value=1",
            "api.validation.size.minimum=1",
            "api.validation.size.maximum=50",
            "api.validation.size.default-value=10",
            "api.validation.search.min-length=3",
            "api.validation.search.max-length=20",
            "api.validation.category.min-length=4",
            "api.validation.category.max-length=15"
    };

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    ConfigurationPropertiesAutoConfiguration.class,
                    ValidationAutoConfiguration.class))
            .withUserConfiguration(TestConfig.class);

    @Test
    void bindsEveryConfiguredBoundary() {
        runner.withPropertyValues(VALID_RULES).run(context -> {
            assertThat(context).hasNotFailed();
            ValidationRulesProperties rules = context.getBean(ValidationRulesProperties.class);

            assertThat(rules.productId().minimum()).isEqualTo(2L);
            assertThat(rules.page().minimum()).isEqualTo(1);
            assertThat(rules.size().minimum()).isEqualTo(1);
            assertThat(rules.size().maximum()).isEqualTo(50);
            assertThat(rules.search().minLength()).isEqualTo(3);
            assertThat(rules.search().maxLength()).isEqualTo(20);
            assertThat(rules.category().minLength()).isEqualTo(4);
            assertThat(rules.category().maxLength()).isEqualTo(15);
        });
    }

    @Test
    void resolvesDefaultValues() {
        runner.withPropertyValues(VALID_RULES).run(context -> {
            ValidationRulesProperties rules = context.getBean(ValidationRulesProperties.class);

            assertThat(rules.resolvePage(null)).isEqualTo(1);
            assertThat(rules.resolveSize(null)).isEqualTo(10);
            assertThat(rules.resolvePage(7)).isEqualTo(7);
            assertThat(rules.resolveSize(25)).isEqualTo(25);
        });
    }

    @Test
    void rejectsANonPositiveSizeMaximum() {
        runner.withPropertyValues(without(VALID_RULES, "api.validation.size.maximum"))
                .withPropertyValues("api.validation.size.maximum=0")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .rootCause()
                            .hasMessageContaining("maximum");
                });
    }

    @Test
    void rejectsANegativeMinimum() {
        runner.withPropertyValues(without(VALID_RULES, "api.validation.product-id.minimum"))
                .withPropertyValues("api.validation.product-id.minimum=-1")
                .run(context -> assertThat(context).hasFailed());
    }

    @Test
    void rejectsAMissingRule() {
        runner.withPropertyValues(without(VALID_RULES, "api.validation.search.min-length"))
                .run(context -> assertThat(context).hasFailed());
    }

    private static String[] without(String[] values, String keyToRemove) {
        return java.util.Arrays.stream(values)
                .filter(value -> !value.startsWith(keyToRemove + "="))
                .toArray(String[]::new);
    }

    @EnableConfigurationProperties(ValidationRulesProperties.class)
    static class TestConfig {
    }
}