package com.backend.hamilton.application.port.out;

import com.backend.hamilton.domain.model.Product;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.math.BigDecimal;
import java.util.List;

/**
 * Test configuration providing a stub implementation of ProductCatalogPort.
 * This allows the application context to load during tests without requiring
 * the infrastructure layer implementation.
 */
@TestConfiguration
public class TestProductCatalogConfig {

    @Bean
    @Primary
    public ProductCatalogPort testProductCatalogPort() {
        return new ProductCatalogPort() {
            @Override
            public List<Product> findAll() {
                return List.of(
                        new Product(
                                1L,
                                "Test Product",
                                "Test Description",
                                BigDecimal.valueOf(99.99),
                                BigDecimal.valueOf(4.5),
                                "https://example.com/thumbnail.jpg",
                                List.of("https://example.com/image1.jpg"),
                                "Test Category",
                                "Test Brand"
                        )
                );
            }

            @Override
            public Product findById(Long id) {
                return new Product(
                        id,
                        "Test Product",
                        "Test Description",
                        BigDecimal.valueOf(99.99),
                        BigDecimal.valueOf(4.5),
                        "https://example.com/thumbnail.jpg",
                        List.of("https://example.com/image1.jpg"),
                        "Test Category",
                        "Test Brand"
                );
            }
        };
    }
}
