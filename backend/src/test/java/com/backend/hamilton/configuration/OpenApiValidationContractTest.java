package com.backend.hamilton.configuration;

import com.backend.hamilton.configuration.properties.ValidationRulesProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Contract test proving that the limits published in the OpenAPI document are the very
 * same values the validators enforce, both taken from {@code api.validation}.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiValidationContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ValidationRulesProperties rules;

    @Test
    void shouldPublishTheConfiguredCollectionBoundaries() throws Exception {
        JsonNode parameters = parametersOf("/api/products");

        assertThat(number(parameters, "page", "minimum")).isEqualTo(rules.page().minimum().longValue());
        assertThat(number(parameters, "page", "default")).isEqualTo(rules.page().defaultValue().longValue());

        assertThat(number(parameters, "size", "minimum")).isEqualTo(rules.size().minimum().longValue());
        assertThat(number(parameters, "size", "maximum")).isEqualTo(rules.size().maximum().longValue());
        assertThat(number(parameters, "size", "default")).isEqualTo(rules.size().defaultValue().longValue());

        assertThat(number(parameters, "search", "minLength")).isEqualTo(rules.search().minLength().longValue());
        assertThat(number(parameters, "search", "maxLength")).isEqualTo(rules.search().maxLength().longValue());

        assertThat(number(parameters, "category", "minLength")).isEqualTo(rules.category().minLength().longValue());
        assertThat(number(parameters, "category", "maxLength")).isEqualTo(rules.category().maxLength().longValue());
    }

    @Test
    void shouldPublishTheConfiguredProductIdMinimum() throws Exception {
        JsonNode parameters = parametersOf("/api/products/{id}");

        assertThat(number(parameters, "id", "minimum")).isEqualTo(rules.productId().minimum().longValue());
    }

    @Test
    void shouldPublishTheConfiguredBasePath() throws Exception {
        JsonNode document = openApiDocument();

        assertThat(document.at("/paths").has("/api/products")).isTrue();
        assertThat(document.at("/paths").has("/api/products/{id}")).isTrue();
    }

    private JsonNode parametersOf(String path) throws Exception {
        JsonNode paths = openApiDocument().at("/paths");
        JsonNode operation = paths.get(path).get("get");
        assertThat(operation).as("GET %s operation", path).isNotNull();
        return operation.get("parameters");
    }

    private JsonNode openApiDocument() throws Exception {
        String json = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(json);
    }

    private static long number(JsonNode parameters, String parameterName, String field) {
        for (JsonNode parameter : parameters) {
            if (parameterName.equals(parameter.get("name").asText())) {
                JsonNode value = parameter.get("schema").get(field);
                assertThat(value)
                        .as("schema field '%s' of parameter '%s'", field, parameterName)
                        .isNotNull();
                return value.asLong();
            }
        }
        throw new AssertionError("parameter not found: " + parameterName);
    }
}