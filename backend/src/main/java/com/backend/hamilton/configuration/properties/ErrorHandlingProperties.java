package com.backend.hamilton.configuration.properties;

import com.backend.hamilton.domain.exception.ErrorCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;

import java.util.Map;
import java.util.Optional;

/**
 * Error catalog of the API, bound from {@code api.errors.definitions}.
 *
 * <p>Every error the API can return is declared here with its stable code, its message,
 * its HTTP status and the RFC 7807 title/URI used to render the problem detail.
 * Messages may contain {@code {placeholder}} tokens that are resolved against the
 * details carried by the raised exception.
 */
@ConfigurationProperties(prefix = "api.errors")
@Validated
public record ErrorHandlingProperties(
        @NotEmpty @Valid Map<String, ErrorDefinition> definitions
) {

    /**
     * Definition of a single error type.
     *
     * @param code stable, machine readable error code
     * @param message human readable message, may contain {@code {placeholder}} tokens
     * @param httpStatus numeric HTTP status returned by the API for this error
     * @param title short title used by the problem detail
     * @param uri documentation URI of the error type
     */
    public record ErrorDefinition(
            @NotBlank String code,
            @NotBlank String message,
            @NotNull @Min(100) @Max(599) Integer httpStatus,
            String title,
            String uri
    ) {

        /**
         * @return the HTTP status as a Spring enum
         */
        public HttpStatus status() {
            return HttpStatus.valueOf(httpStatus);
        }

        /**
         * Renders the configured message, replacing every {@code {placeholder}} with the
         * matching detail. Placeholders without a matching detail are left untouched.
         *
         * @param details values collected while the error was being produced
         * @return the resolved message
         */
        public String render(Map<String, String> details) {
            String rendered = message;
            for (Map.Entry<String, String> detail : details.entrySet()) {
                rendered = rendered.replace("{" + detail.getKey() + "}", detail.getValue());
            }
            return rendered;
        }
    }

    /**
     * Looks up the definition declared for the given error code.
     *
     * @param code error code raised by the application
     * @return the matching definition
     * @throws IllegalStateException when the configuration has no entry for the code
     */
    public ErrorDefinition resolve(ErrorCode code) {
        return find(code.configKey())
                .orElseThrow(() -> new IllegalStateException(
                        "No error definition configured for key '" + code.configKey()
                                + "' (api.errors.definitions)"));
    }

    /**
     * Looks up the definition declared for the given configuration key.
     *
     * @param key key under {@code api.errors.definitions}
     * @return the matching definition, empty when not configured
     */
    public Optional<ErrorDefinition> find(String key) {
        return Optional.ofNullable(definitions.get(key));
    }
}