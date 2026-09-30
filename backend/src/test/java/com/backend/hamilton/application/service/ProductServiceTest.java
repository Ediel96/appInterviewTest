package com.backend.hamilton.application.service;

import com.backend.hamilton.application.port.out.ProductCatalogPort;
import com.backend.hamilton.domain.exception.InvalidProductIdException;
import com.backend.hamilton.domain.exception.ProductNotFoundException;
import com.backend.hamilton.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductService.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductCatalogPort productCatalogPort;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productCatalogPort);
    }

    @Test
    void shouldGetAllProducts() {
        List<Product> expectedProducts = List.of(
                createProduct(1L, "Product 1"),
                createProduct(2L, "Product 2")
        );
        when(productCatalogPort.findAll()).thenReturn(expectedProducts);

        List<Product> result = productService.getProducts();

        assertEquals(2, result.size());
        assertEquals(expectedProducts, result);
        verify(productCatalogPort, times(1)).findAll();
    }

    @Test
    void shouldGetProductById() {
        Product expectedProduct = createProduct(1L, "Test Product");
        when(productCatalogPort.findById(1L)).thenReturn(expectedProduct);

        Product result = productService.getProductById(1L);

        assertEquals(expectedProduct, result);
        assertEquals(1L, result.id());
        assertEquals("Test Product", result.title());
        verify(productCatalogPort, times(1)).findById(1L);
    }

    @Test
    void shouldRejectNullId() {
        InvalidProductIdException exception = assertThrows(
                InvalidProductIdException.class,
                () -> productService.getProductById(null)
        );

        assertEquals("Product ID cannot be null", exception.getMessage());
        verify(productCatalogPort, never()).findById(any());
    }

    @Test
    void shouldRejectZeroId() {
        InvalidProductIdException exception = assertThrows(
                InvalidProductIdException.class,
                () -> productService.getProductById(0L)
        );

        assertEquals("Product ID must be greater than zero", exception.getMessage());
        verify(productCatalogPort, never()).findById(any());
    }

    @Test
    void shouldRejectNegativeId() {
        InvalidProductIdException exception = assertThrows(
                InvalidProductIdException.class,
                () -> productService.getProductById(-1L)
        );

        assertEquals("Product ID must be greater than zero", exception.getMessage());
        verify(productCatalogPort, never()).findById(any());
    }

    @Test
    void shouldPropagateProductNotFoundException() {
        when(productCatalogPort.findById(999L))
                .thenThrow(new ProductNotFoundException("Product with ID 999 not found"));

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(999L)
        );

        assertEquals("Product with ID 999 not found", exception.getMessage());
        verify(productCatalogPort, times(1)).findById(999L);
    }

    @Test
    void shouldNotModifyProductsReceived() {
        Product originalProduct = createProduct(1L, "Original Product");
        when(productCatalogPort.findById(1L)).thenReturn(originalProduct);

        Product result = productService.getProductById(1L);

        // Verify it's the same instance (not modified or copied)
        assertSame(originalProduct, result);
    }

    @Test
    void shouldReturnEmptyListWhenNoProducts() {
        when(productCatalogPort.findAll()).thenReturn(List.of());

        List<Product> result = productService.getProducts();

        assertTrue(result.isEmpty());
        verify(productCatalogPort, times(1)).findAll();
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
