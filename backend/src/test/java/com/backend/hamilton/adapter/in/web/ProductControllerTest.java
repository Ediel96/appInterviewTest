package com.backend.hamilton.adapter.in.web;

import com.backend.hamilton.application.port.in.GetProductByIdUseCase;
import com.backend.hamilton.application.port.in.ListProductsUseCase;
import com.backend.hamilton.configuration.properties.ErrorHandlingProperties;
import com.backend.hamilton.configuration.properties.ValidationRulesProperties;
import com.backend.hamilton.domain.exception.ExternalServiceException;
import com.backend.hamilton.domain.exception.ExternalServiceTimeoutException;
import com.backend.hamilton.domain.exception.InvalidProductIdException;
import com.backend.hamilton.domain.exception.ProductNotFoundException;
import com.backend.hamilton.domain.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web layer tests for ProductController.
 */
@WebMvcTest(ProductController.class)
@EnableConfigurationProperties({ValidationRulesProperties.class, ErrorHandlingProperties.class})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListProductsUseCase listProductsUseCase;

    @MockitoBean
    private GetProductByIdUseCase getProductByIdUseCase;

    @Test
    void shouldReturnAllProductsWith200() throws Exception {
        List<Product> products = List.of(
                createProduct(1L, "Product 1"),
                createProduct(2L, "Product 2")
        );
        when(listProductsUseCase.list(any())).thenReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.products", hasSize(2)))
                .andExpect(jsonPath("$.total", is(2)))
                .andExpect(jsonPath("$.products[0].id", is(1)))
                .andExpect(jsonPath("$.products[0].title", is("Product 1")))
                .andExpect(jsonPath("$.products[1].id", is(2)))
                .andExpect(jsonPath("$.products[1].title", is("Product 2")));

        verify(listProductsUseCase, times(1)).list(any());
    }

    @Test
    void shouldApplyConfiguredDefaultsWhenNoFilterIsSent() throws Exception {
        when(listProductsUseCase.list(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());

        verify(listProductsUseCase).list(argThat(query -> query.page() == 0 && query.size() == 0));
    }

    @Test
    void shouldForwardFiltersToTheUseCase() throws Exception {
        when(listProductsUseCase.list(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/products")
                        .param("page", "2")
                        .param("size", "10")
                        .param("search", "mascara")
                        .param("category", "beauty"))
                .andExpect(status().isOk());

        verify(listProductsUseCase).list(argThat(query ->
                query.page() == 2
                        && query.size() == 10
                        && "mascara".equals(query.search())
                        && "beauty".equals(query.category())));
    }

    @Test
    void shouldReturnProductByIdWith200() throws Exception {
        Product product = createProduct(1L, "Test Product");
        when(getProductByIdUseCase.getProductById(1L)).thenReturn(product);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Product")))
                .andExpect(jsonPath("$.description", is("Description for Test Product")))
                .andExpect(jsonPath("$.price", is(99.99)))
                .andExpect(jsonPath("$.rating", is(4.5)))
                .andExpect(jsonPath("$.category", is("electronics")))
                .andExpect(jsonPath("$.brand", is("Test Brand")));

        verify(getProductByIdUseCase, times(1)).getProductById(1L);
    }

    @Test
    void shouldReturn400ForInvalidProductId() throws Exception {
        when(getProductByIdUseCase.getProductById(1L))
                .thenThrow(new InvalidProductIdException("Product ID must be greater than zero"));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.code", is("INVALID_PRODUCT_ID")))
                .andExpect(jsonPath("$.message", containsString("Product ID")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));

        verify(getProductByIdUseCase, times(1)).getProductById(1L);
    }

    @Test
    void shouldRejectProductIdBelowConfiguredMinimumBeforeReachingTheUseCase() throws Exception {
        mockMvc.perform(get("/api/products/0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.code", is("VALIDATION_FAILED")))
                .andExpect(jsonPath("$.message", containsString("Validation failed")));

        verifyNoInteractions(getProductByIdUseCase);
    }

    @Test
    void shouldRejectPageSizeAboveConfiguredMaximum() throws Exception {
        mockMvc.perform(get("/api/products").param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.code", is("VALIDATION_FAILED")))
                .andExpect(jsonPath("$.message", containsString("size")));

        verifyNoInteractions(listProductsUseCase);
    }

    @Test
    void shouldRejectNegativePageNumber() throws Exception {
        mockMvc.perform(get("/api/products").param("page", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("VALIDATION_FAILED")))
                .andExpect(jsonPath("$.message", containsString("page")));

        verifyNoInteractions(listProductsUseCase);
    }

    @Test
    void shouldRejectSearchTermShorterThanConfiguredMinimum() throws Exception {
        mockMvc.perform(get("/api/products").param("search", "a"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("VALIDATION_FAILED")))
                .andExpect(jsonPath("$.message", containsString("search")));

        verifyNoInteractions(listProductsUseCase);
    }

    @Test
    void shouldReturn400ForMalformedProductId() throws Exception {
        mockMvc.perform(get("/api/products/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.code", is("TYPE_MISMATCH")))
                .andExpect(jsonPath("$.message", containsString("id")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));

        verifyNoInteractions(getProductByIdUseCase);
    }

    @Test
    void shouldReturn404ForNonExistentProduct() throws Exception {
        when(getProductByIdUseCase.getProductById(999L))
                .thenThrow(new ProductNotFoundException("Product with ID 999 not found"));

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.code", is("PRODUCT_NOT_FOUND")))
                .andExpect(jsonPath("$.message", containsString("not found")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));

        verify(getProductByIdUseCase, times(1)).getProductById(999L);
    }

    @Test
    void shouldReturn502ForExternalServiceError() throws Exception {
        when(listProductsUseCase.list(any()))
                .thenThrow(new ExternalServiceException("External service error"));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isBadGateway())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(502)))
                .andExpect(jsonPath("$.code", is("EXTERNAL_SERVICE_ERROR")))
                .andExpect(jsonPath("$.message", containsString("External service")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));

        verify(listProductsUseCase, times(1)).list(any());
    }

    @Test
    void shouldReturn504ForExternalServiceTimeout() throws Exception {
        when(getProductByIdUseCase.getProductById(1L))
                .thenThrow(new ExternalServiceTimeoutException("Timeout accessing external service"));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isGatewayTimeout())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(504)))
                .andExpect(jsonPath("$.code", is("EXTERNAL_SERVICE_TIMEOUT")))
                .andExpect(jsonPath("$.message", containsString("Timeout")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));

        verify(getProductByIdUseCase, times(1)).getProductById(1L);
    }

    @Test
    void shouldReturn500ForUnexpectedError() throws Exception {
        when(listProductsUseCase.list(any())).thenThrow(new IllegalStateException("boom"));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.code", is("UNEXPECTED_ERROR")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void shouldReturnJsonContentType() throws Exception {
        List<Product> products = List.of(createProduct(1L, "Product 1"));
        when(listProductsUseCase.list(any())).thenReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldMaintainApiErrorResponseStructure() throws Exception {
        when(getProductByIdUseCase.getProductById(999L))
                .thenThrow(new ProductNotFoundException("Product not found"));

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").isNumber())
                .andExpect(jsonPath("$.code").isString())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.path").isString())
                .andExpect(jsonPath("$.timestamp").isString());
    }

    @Test
    void shouldReturnEmptyListWith200() throws Exception {
        when(listProductsUseCase.list(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.products", hasSize(0)))
                .andExpect(jsonPath("$.total", is(0)));
    }

    @Test
    void shouldIncludeAllProductFieldsInResponse() throws Exception {
        Product product = new Product(
                1L,
                "Complete Product",
                "Complete Description",
                BigDecimal.valueOf(99.99),
                BigDecimal.valueOf(4.5),
                "thumbnail.jpg",
                List.of("image1.jpg", "image2.jpg"),
                "electronics",
                "Complete Brand"
        );
        when(getProductByIdUseCase.getProductById(1L)).thenReturn(product);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.price").exists())
                .andExpect(jsonPath("$.rating").exists())
                .andExpect(jsonPath("$.thumbnail").exists())
                .andExpect(jsonPath("$.images").isArray())
                .andExpect(jsonPath("$.category").exists())
                .andExpect(jsonPath("$.brand").exists());
    }

    private Product createProduct(Long id, String title) {
        return new Product(
                id,
                title,
                "Description for " + title,
                BigDecimal.valueOf(99.99),
                BigDecimal.valueOf(4.5),
                "thumbnail.jpg",
                List.of("image1.jpg"),
                "electronics",
                "Test Brand"
        );
    }
}