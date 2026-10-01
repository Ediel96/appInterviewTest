package com.backend.hamilton.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for the OpenAPI document generated from the Java controller.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGenerateOpenApiDocumentationForProductController() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.info.title", is("MiniStore API")))
                .andExpect(jsonPath("$.info.description",
                        is("API intermediaria entre la aplicación React Native y DummyJSON")))
                .andExpect(jsonPath("$.info.version", is("1.0.0")))
                .andExpect(jsonPath("$.servers[0].url", is("http://localhost:8080")))
                .andExpect(jsonPath("$.servers[0].description", is("Entorno local")))
                .andExpect(jsonPath("$.paths['/api/products'].get.summary", is("Obtener todos los productos")))
                .andExpect(jsonPath("$.paths['/api/products'].get.responses['200']").exists())
                .andExpect(jsonPath("$.paths['/api/products'].get.responses['502']").exists())
                .andExpect(jsonPath("$.paths['/api/products'].get.responses['504']").exists())
                .andExpect(jsonPath("$.paths['/api/products/{id}'].get.summary", is("Obtener producto por ID")))
                .andExpect(jsonPath("$.paths['/api/products/{id}'].get.parameters[0].name", is("id")))
                .andExpect(jsonPath("$.paths['/api/products/{id}'].get.responses['400']").exists())
                .andExpect(jsonPath("$.paths['/api/products/{id}'].get.responses['404']").exists())
                .andExpect(jsonPath("$.paths['/api/products/{id}'].get.responses['502']").exists())
                .andExpect(jsonPath("$.paths['/api/products/{id}'].get.responses['504']").exists())
                .andExpect(jsonPath("$.components.schemas.ProductListResponse").exists())
                .andExpect(jsonPath("$.components.schemas.ProductResponse").exists())
                .andExpect(jsonPath("$.components.schemas.ApiErrorResponse").exists());
    }
}
