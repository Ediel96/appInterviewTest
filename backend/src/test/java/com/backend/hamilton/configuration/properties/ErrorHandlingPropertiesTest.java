package com.backend.hamilton.configuration.properties;

import com.backend.hamilton.domain.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies that the error catalog is complete and drives the API responses.
 */
@SpringBootTest
@ActiveProfiles("test")
class ErrorHandlingPropertiesTest {

    @Autowired
    private ErrorHandlingProperties properties;

    @Test
    void everyErrorCodeHasAConfiguredDefinition() {
        for (ErrorCode code : ErrorCode.values()) {
            assertThat(properties.find(code.configKey()))
                    .as("definition for %s", code.configKey())
                    .isPresent();
            assertThat(properties.resolve(code).code()).isNotBlank();
            assertThat(properties.resolve(code).message()).isNotBlank();
            assertThat(properties.resolve(code).status().isError()).isTrue();
        }
    }

    @Test
    void mapsEachErrorCodeToItsConfiguredHttpStatus() {
        assertThat(properties.resolve(ErrorCode.INVALID_PRODUCT_ID).status()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(properties.resolve(ErrorCode.VALIDATION_FAILED).status()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(properties.resolve(ErrorCode.TYPE_MISMATCH).status()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(properties.resolve(ErrorCode.PRODUCT_NOT_FOUND).status()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(properties.resolve(ErrorCode.EXTERNAL_SERVICE_ERROR).status()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(properties.resolve(ErrorCode.EXTERNAL_SERVICE_TIMEOUT).status()).isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
        assertThat(properties.resolve(ErrorCode.UNEXPECTED_ERROR).status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void rendersPlaceholdersFromExceptionDetails() {
        ErrorHandlingProperties.ErrorDefinition definition =
                properties.resolve(ErrorCode.PRODUCT_NOT_FOUND);

        assertThat(definition.render(Map.of("id", "999")))
                .isEqualTo("Product with ID 999 not found");
    }

    @Test
    void leavesUnknownPlaceholdersUntouched() {
        ErrorHandlingProperties.ErrorDefinition definition = new ErrorHandlingProperties.ErrorDefinition(
                "CUSTOM", "Missing {value} here", 400, "custom", null);

        assertThat(definition.render(Map.of())).isEqualTo("Missing {value} here");
    }

    @Test
    void failsFastWhenAnErrorCodeIsNotConfigured() {
        ErrorHandlingProperties empty = new ErrorHandlingProperties(Map.of());

        assertThatThrownBy(() -> empty.resolve(ErrorCode.PRODUCT_NOT_FOUND))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("product-not-found");
    }
}