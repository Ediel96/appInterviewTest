package com.backend.hamilton.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Product domain model.
 */
class ProductTest {

    @Test
    void shouldCreateProductWithAllFields() {
        List<String> images = List.of("image1.jpg", "image2.jpg");

        Product product = new Product(
                1L,
                "Test Product",
                "Test Description",
                BigDecimal.valueOf(99.99),
                BigDecimal.valueOf(4.5),
                "thumbnail.jpg",
                images,
                "electronics",
                "Test Brand"
        );

        assertEquals(1L, product.id());
        assertEquals("Test Product", product.title());
        assertEquals("Test Description", product.description());
        assertEquals(BigDecimal.valueOf(99.99), product.price());
        assertEquals(BigDecimal.valueOf(4.5), product.rating());
        assertEquals("thumbnail.jpg", product.thumbnail());
        assertEquals(2, product.images().size());
        assertEquals("electronics", product.category());
        assertEquals("Test Brand", product.brand());
    }

    @Test
    void shouldAllowNullBrand() {
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

        assertNull(product.brand());
    }

    @Test
    void shouldRejectNullId() {
        assertThrows(NullPointerException.class, () -> new Product(
                null,
                "Test Product",
                "Test Description",
                BigDecimal.valueOf(99.99),
                BigDecimal.valueOf(4.5),
                "thumbnail.jpg",
                List.of("image1.jpg"),
                "electronics",
                "Test Brand"
        ));
    }

    @Test
    void shouldRejectNullTitle() {
        assertThrows(NullPointerException.class, () -> new Product(
                1L,
                null,
                "Test Description",
                BigDecimal.valueOf(99.99),
                BigDecimal.valueOf(4.5),
                "thumbnail.jpg",
                List.of("image1.jpg"),
                "electronics",
                "Test Brand"
        ));
    }

    @Test
    void shouldRejectNullImages() {
        assertThrows(NullPointerException.class, () -> new Product(
                1L,
                "Test Product",
                "Test Description",
                BigDecimal.valueOf(99.99),
                BigDecimal.valueOf(4.5),
                "thumbnail.jpg",
                null,
                "electronics",
                "Test Brand"
        ));
    }

    @Test
    void shouldCreateDefensiveCopyOfImages() {
        List<String> originalImages = new ArrayList<>();
        originalImages.add("image1.jpg");
        originalImages.add("image2.jpg");

        Product product = new Product(
                1L,
                "Test Product",
                "Test Description",
                BigDecimal.valueOf(99.99),
                BigDecimal.valueOf(4.5),
                "thumbnail.jpg",
                originalImages,
                "electronics",
                "Test Brand"
        );

        // Modify original list
        originalImages.add("image3.jpg");

        // Product's images should not be affected
        assertEquals(2, product.images().size());
    }

    @Test
    void shouldReturnDefensiveCopyFromGetter() {
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

        List<String> images1 = product.images();
        List<String> images2 = product.images();

        // Should return different instances
        assertNotSame(images1, images2);
        assertEquals(images1, images2);
    }

    @Test
    void shouldAcceptEmptyImagesList() {
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

        assertTrue(product.images().isEmpty());
    }
}
