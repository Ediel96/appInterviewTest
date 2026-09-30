package com.backend.hamilton.adapter.out.dummyjson;

import com.backend.hamilton.domain.model.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DummyJsonProductMapper.
 */
class DummyJsonProductMapperTest {

    @Test
    void shouldMapDtoToDomain() {
        DummyJsonProductDto dto = new DummyJsonProductDto(
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

        Product product = DummyJsonProductMapper.toDomain(dto);

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
    void shouldConvertNullImagesToEmptyList() {
        DummyJsonProductDto dto = new DummyJsonProductDto(
                1L,
                "Test Product",
                "Test Description",
                BigDecimal.valueOf(99.99),
                BigDecimal.valueOf(4.5),
                "thumbnail.jpg",
                null,
                "electronics",
                "Test Brand"
        );

        Product product = DummyJsonProductMapper.toDomain(dto);

        assertNotNull(product.images());
        assertTrue(product.images().isEmpty());
    }

    @Test
    void shouldAllowNullBrand() {
        DummyJsonProductDto dto = new DummyJsonProductDto(
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

        Product product = DummyJsonProductMapper.toDomain(dto);

        assertNull(product.brand());
    }

    @Test
    void shouldConvertNullPriceToZero() {
        DummyJsonProductDto dto = new DummyJsonProductDto(
                1L,
                "Test Product",
                "Test Description",
                null,
                BigDecimal.valueOf(4.5),
                "thumbnail.jpg",
                List.of("image1.jpg"),
                "electronics",
                "Test Brand"
        );

        Product product = DummyJsonProductMapper.toDomain(dto);

        assertEquals(BigDecimal.ZERO, product.price());
    }

    @Test
    void shouldConvertNullRatingToZero() {
        DummyJsonProductDto dto = new DummyJsonProductDto(
                1L,
                "Test Product",
                "Test Description",
                BigDecimal.valueOf(99.99),
                null,
                "thumbnail.jpg",
                List.of("image1.jpg"),
                "electronics",
                "Test Brand"
        );

        Product product = DummyJsonProductMapper.toDomain(dto);

        assertEquals(BigDecimal.ZERO, product.rating());
    }

    @Test
    void shouldHandleEmptyImagesList() {
        DummyJsonProductDto dto = new DummyJsonProductDto(
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

        Product product = DummyJsonProductMapper.toDomain(dto);

        assertNotNull(product.images());
        assertTrue(product.images().isEmpty());
    }

    @Test
    void shouldHandleAllNullableFieldsAsNull() {
        DummyJsonProductDto dto = new DummyJsonProductDto(
                1L,
                "Test Product",
                "Test Description",
                null,
                null,
                "thumbnail.jpg",
                null,
                "electronics",
                null
        );

        Product product = DummyJsonProductMapper.toDomain(dto);

        assertEquals(BigDecimal.ZERO, product.price());
        assertEquals(BigDecimal.ZERO, product.rating());
        assertTrue(product.images().isEmpty());
        assertNull(product.brand());
    }
}
