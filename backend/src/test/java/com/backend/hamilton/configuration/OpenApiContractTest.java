package com.backend.hamilton.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for OpenAPI documentation contract.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnOpenApiDocsWithStatus200() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldContainProductsEndpoint() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/products']").exists())
                .andExpect(jsonPath("$.paths['/api/products'].get").exists());
    }

    @Test
    void shouldContainProductByIdEndpoint() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/products/{id}']").exists())
                .andExpect(jsonPath("$.paths['/api/products/{id}'].get").exists());
    }

    @Test
    void shouldContainProductResponseSchema() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.schemas.ProductResponse").exists())
                .andExpect(jsonPath("$.components.schemas.ProductResponse.properties.id").exists())
                .andExpect(jsonPath("$.components.schemas.ProductResponse.properties.title").exists())
                .andExpect(jsonPath("$.components.schemas.ProductResponse.properties.description").exists())
                .andExpect(jsonPath("$.components.schemas.ProductResponse.properties.price").exists())
                .andExpect(jsonPath("$.components.schemas.ProductResponse.properties.rating").exists())
                .andExpect(jsonPath("$.components.schemas.ProductResponse.properties.thumbnail").exists())
                .andExpect(jsonPath("$.components.schemas.ProductResponse.properties.images").exists())
                .andExpect(jsonPath("$.components.schemas.ProductResponse.properties.category").exists())
                .andExpect(jsonPath("$.components.schemas.ProductResponse.properties.brand").exists());
    }

    @Test
    void shouldContainProductListResponseSchema() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.schemas.ProductListResponse").exists())
                .andExpect(jsonPath("$.components.schemas.ProductListResponse.properties.products").exists())
                .andExpect(jsonPath("$.components.schemas.ProductListResponse.properties.total").exists());
    }

    @Test
    void shouldContainApiErrorResponseSchema() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.schemas.ApiErrorResponse").exists())
                .andExpect(jsonPath("$.components.schemas.ApiErrorResponse.properties.status").exists())
                .andExpect(jsonPath("$.components.schemas.ApiErrorResponse.properties.message").exists())
                .andExpect(jsonPath("$.components.schemas.ApiErrorResponse.properties.timestamp").exists());
    }

    @Test
    void shouldDocument200ResponseForGetAllProducts() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/products'].get.responses['200']").exists())
                .andExpect(jsonPath("$.paths['/api/products'].get.responses['200'].description").exists());
    }

    @Test
    void shouldDocument200ResponseForGetProductById() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/products/{id}'].get.responses['200']").exists())
                .andExpect(jsonPath("$.paths['/api/products/{id}'].get.responses['200'].description").exists());
    }

    @Test
    void shouldDocumentErrorResponsesForGetProductById() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/products/{id}'].get.responses['400']").exists())
                .andExpect(jsonPath("$.paths['/api/products/{id}'].get.responses['404']").exists())
                .andExpect(jsonPath("$.paths['/api/products/{id}'].get.responses['502']").exists())
                .andExpect(jsonPath("$.paths['/api/products/{id}'].get.responses['504']").exists());
    }

    @Test
    void shouldDocumentErrorResponsesForGetAllProducts() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/products'].get.responses['502']").exists())
                .andExpect(jsonPath("$.paths['/api/products'].get.responses['504']").exists());
    }

    @Test
    void shouldHaveCorrectApiInfo() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title", is("MiniStore API")))
                .andExpect(jsonPath("$.info.description", is("Product catalog API powered by DummyJSON")))
                .andExpect(jsonPath("$.info.version", is("1.0.0")));
    }

    @Test
    void shouldHaveCorrectServerConfiguration() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.servers[0].url", is("http://localhost:8080")))
                .andExpect(jsonPath("$.servers[0].description", is("Development server")));
    }

    @Test
    void shouldHaveProductsTag() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tags[?(@.name == 'Products')]").exists())
                .andExpect(jsonPath("$.tags[?(@.name == 'Products')].description",
                        contains("Product catalog operations")));
    }
}
