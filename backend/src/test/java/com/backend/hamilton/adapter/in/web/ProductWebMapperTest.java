package com.backend.hamilton.adapter.in.web;

import com.backend.hamilton.adapter.in.web.dto.ProductListResponse;
import com.backend.hamilton.adapter.in.web.dto.ProductResponse;
import com.backend.hamilton.domain.model.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ProductWebMapper.
 */
class ProductWebMapperTest {

    @Test
    void shouldMapProductToResponse() {
        Product product = new Product(
                1L,
                "Test Product",
                "Test Description",
                BigDecimal.valueOf(99.99),
                BigDecimal.valueOf(4.5),
                "thumbnail.jpg",
                List.of("image1.jpg", "image2.jpg"),
                "electronics",
                "Test Brand"
        );

        ProductResponse response = ProductWebMapper.toResponse(product);

        assertEquals(1L, response.id());
        assertEquals("Test Product", response.title());
        assertEquals("Test Description", response.description());
        assertEquals(BigDecimal.valueOf(99.99), response.price());
        assertEquals(BigDecimal.valueOf(4.5), response.rating());
        assertEquals("thumbnail.jpg", response.thumbnail());
        assertEquals(2, response.images().size());
        assertEquals("electronics", response.category());
        assertEquals("Test Brand", response.brand());
    }

    @Test
    void shouldMapProductWithNullBrand() {
        Product product = new Product(
                1L,
                "Test Product",
                "Test Description",
                BigDecimal.valueOf(99.99),
                BigDecimal.valueOf(4.5),
                "thumbnail.jpg",
                List.of("image1.jpg"),
                "electronics",
                null
        );

        ProductResponse response = ProductWebMapper.toResponse(product);

        assertNull(response.brand());
    }

    @Test
    void shouldMapProductWithEmptyImages() {
        Product product = new Product(
                1L,
                "Test Product",
                "Test Description",
                BigDecimal.valueOf(99.99),
                BigDecimal.valueOf(4.5),
                "thumbnail.jpg",
                List.of(),
                "electronics",
                "Test Brand"
        );

        ProductResponse response = ProductWebMapper.toResponse(product);

        assertNotNull(response.images());
        assertTrue(response.images().isEmpty());
    }

    @Test
    void shouldMapProductListToListResponse() {
        List<Product> products = List.of(
                createProduct(1L, "Product 1"),
                createProduct(2L, "Product 2"),
                createProduct(3L, "Product 3")
        );

        ProductListResponse response = ProductWebMapper.toListResponse(products);

        assertEquals(3, response.total());
        assertEquals(3, response.products().size());
        assertEquals(1L, response.products().get(0).id());
        assertEquals("Product 1", response.products().get(0).title());
        assertEquals(2L, response.products().get(1).id());
        assertEquals("Product 2", response.products().get(1).title());
    }

    @Test
    void shouldMapEmptyListToEmptyResponse() {
        List<Product> products = List.of();

        ProductListResponse response = ProductWebMapper.toListResponse(products);

        assertEquals(0, response.total());
        assertTrue(response.products().isEmpty());
    }

    @Test
    void shouldCalculateCorrectTotal() {
        List<Product> products = List.of(
                createProduct(1L, "Product 1"),
                createProduct(2L, "Product 2")
        );

        ProductListResponse response = ProductWebMapper.toListResponse(products);

        assertEquals(products.size(), response.total());
    }

    @Test
    void shouldPreserveAllProductFieldsInListResponse() {
        Product product = new Product(
                1L,
                "Test Product",
                "Test Description",
                BigDecimal.valueOf(99.99),
                BigDecimal.valueOf(4.5),
                "thumbnail.jpg",
                List.of("image1.jpg", "image2.jpg"),
                "electronics",
                "Test Brand"
        );

        ProductListResponse response = ProductWebMapper.toListResponse(List.of(product));

        ProductResponse productResponse = response.products().get(0);
        assertEquals(1L, productResponse.id());
        assertEquals("Test Product", productResponse.title());
        assertEquals("Test Description", productResponse.description());
        assertEquals(BigDecimal.valueOf(99.99), productResponse.price());
        assertEquals(BigDecimal.valueOf(4.5), productResponse.rating());
        assertEquals("thumbnail.jpg", productResponse.thumbnail());
        assertEquals(2, productResponse.images().size());
        assertEquals("electronics", productResponse.category());
        assertEquals("Test Brand", productResponse.brand());
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
