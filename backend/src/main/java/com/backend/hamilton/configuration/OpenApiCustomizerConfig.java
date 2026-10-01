package com.backend.hamilton.configuration;

import com.backend.hamilton.configuration.properties.ApiDocsProperties;
import com.backend.hamilton.configuration.properties.ValidationRulesProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

/**
 * Publishes the configured validation boundaries in the generated OpenAPI document.
 *
 * <p>Bean Validation annotations cannot read values from configuration, so the numeric
 * limits are not written on the DTOs. This customizer injects them into the OpenAPI
 * parameters from the very same {@link ValidationRulesProperties} bean the validators
 * use, keeping documentation and runtime behaviour in sync by construction.
 */
@Configuration
public class OpenApiCustomizerConfig {

    private static final String PAGE = "page";
    private static final String SIZE = "size";
    private static final String SEARCH = "search";
    private static final String CATEGORY = "category";
    private static final String ID = "id";

    /**
     * Creates the customizer that stamps the configured limits onto the operations.
     *
     * @param docs API paths configuration
     * @param rules validation boundaries configuration
     * @return the OpenAPI customizer
     */
    @Bean
    public OpenApiCustomizer validationBoundariesCustomizer(
            ApiDocsProperties docs,
            ValidationRulesProperties rules) {
        return openApi -> {
            Operation listOperation = operationAt(openApi, docs.endpoints().productsBasePath());
            if (listOperation != null) {
                constrainNumber(listOperation, PAGE,
                        rules.page().minimum(), null, rules.page().defaultValue());
                constrainNumber(listOperation, SIZE,
                        rules.size().minimum(), rules.size().maximum(), rules.size().defaultValue());
                constrainText(listOperation, SEARCH,
                        rules.search().minLength(), rules.search().maxLength());
                constrainText(listOperation, CATEGORY,
                        rules.category().minLength(), rules.category().maxLength());
            }

            Operation detailOperation = operationAt(openApi, docs.productByIdPath());
            if (detailOperation != null) {
                constrainNumber(detailOperation, ID, rules.productId().minimum(), null, null);
            }
        };
    }

    private static Operation operationAt(OpenAPI openApi, String path) {
        if (openApi.getPaths() == null || openApi.getPaths().get(path) == null) {
            return null;
        }
        return openApi.getPaths().get(path).getGet();
    }

    private static void constrainNumber(
            Operation operation,
            String name,
            Number minimum,
            Number maximum,
            Integer defaultValue) {
        parameterNamed(operation, name).ifPresent(parameter -> {
            Schema<?> schema = schemaOf(parameter, name, "integer");
            if (minimum != null) {
                schema.setMinimum(BigDecimal.valueOf(minimum.longValue()));
            }
            if (maximum != null) {
                schema.setMaximum(BigDecimal.valueOf(maximum.longValue()));
            }
            if (defaultValue != null) {
                schema.setDefault(defaultValue);
            }
        });
    }

    private static void constrainText(Operation operation, String name, Integer minLength, Integer maxLength) {
        parameterNamed(operation, name).ifPresent(parameter -> {
            Schema<?> schema = schemaOf(parameter, name, "string");
            if (minLength != null) {
                schema.setMinLength(minLength);
            }
            if (maxLength != null) {
                schema.setMaxLength(maxLength);
            }
        });
    }

    private static java.util.Optional<Parameter> parameterNamed(Operation operation, String name) {
        List<Parameter> parameters = operation.getParameters();
        if (parameters == null) {
            return java.util.Optional.empty();
        }
        return parameters.stream()
                .filter(parameter -> name.equals(parameter.getName()))
                .findFirst();
    }

    private static Schema<?> schemaOf(Parameter parameter, String name, String type) {
        Schema<?> schema = parameter.getSchema();
        if (schema == null) {
            schema = new Schema<>().type(type).name(name);
            parameter.setSchema(schema);
        }
        return schema;
    }
}