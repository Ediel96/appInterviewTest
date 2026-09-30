package com.backend.hamilton.adapter.in.web;

import com.backend.hamilton.application.port.in.GetProductByIdUseCase;
import com.backend.hamilton.application.port.in.GetProductsUseCase;
import com.backend.hamilton.domain.exception.ExternalServiceException;
import com.backend.hamilton.domain.exception.ExternalServiceTimeoutException;
import com.backend.hamilton.domain.exception.InvalidProductIdException;
import com.backend.hamilton.domain.exception.ProductNotFoundException;
import com.backend.hamilton.domain.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web layer tests for ProductController.
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetProductsUseCase getProductsUseCase;

    @MockitoBean
    private GetProductByIdUseCase getProductByIdUseCase;

    @Test
    void shouldReturnAllProductsWith200() throws Exception {
        List<Product> products = List.of(
                createProduct(1L, "Product 1"),
                createProduct(2L, "Product 2")
        );
        when(getProductsUseCase.getProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.products", hasSize(2)))
                .andExpect(jsonPath("$.total", is(2)))
                .andExpect(jsonPath("$.products[0].id", is(1)))
                .andExpect(jsonPath("$.products[0].title", is("Product 1")))
                .andExpect(jsonPath("$.products[1].id", is(2)))
                .andExpect(jsonPath("$.products[1].title", is("Product 2")));

        verify(getProductsUseCase, times(1)).getProducts();
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
        when(getProductByIdUseCase.getProductById(0L))
                .thenThrow(new InvalidProductIdException("Product ID must be greater than zero"));

        mockMvc.perform(get("/api/products/0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("Product ID")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));

        verify(getProductByIdUseCase, times(1)).getProductById(0L);
    }

    @Test
    void shouldReturn404ForNonExistentProduct() throws Exception {
        when(getProductByIdUseCase.getProductById(999L))
                .thenThrow(new ProductNotFoundException("Product with ID 999 not found"));

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("not found")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));

        verify(getProductByIdUseCase, times(1)).getProductById(999L);
    }

    @Test
    void shouldReturn502ForExternalServiceError() throws Exception {
        when(getProductsUseCase.getProducts())
                .thenThrow(new ExternalServiceException("External service error"));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isBadGateway())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(502)))
                .andExpect(jsonPath("$.message", containsString("External service")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));

        verify(getProductsUseCase, times(1)).getProducts();
    }

    @Test
    void shouldReturn504ForExternalServiceTimeout() throws Exception {
        when(getProductByIdUseCase.getProductById(1L))
                .thenThrow(new ExternalServiceTimeoutException("Timeout accessing external service"));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isGatewayTimeout())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(504)))
                .andExpect(jsonPath("$.message", containsString("Timeout")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));

        verify(getProductByIdUseCase, times(1)).getProductById(1L);
    }

    @Test
    void shouldReturnJsonContentType() throws Exception {
        List<Product> products = List.of(createProduct(1L, "Product 1"));
        when(getProductsUseCase.getProducts()).thenReturn(products);

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
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.timestamp").isString());
    }

    @Test
    void shouldReturnEmptyListWith200() throws Exception {
        when(getProductsUseCase.getProducts()).thenReturn(List.of());

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
