package com.backend.hamilton.application.config;

import com.backend.hamilton.application.port.out.ProductCatalogPort;
import com.backend.hamilton.application.service.ProductService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Application layer configuration.
 * Defines beans for use cases and services.
 */
@Configuration
public class ApplicationConfig {

    /**
     * Creates the ProductService bean with explicit dependency injection.
     *
     * @param productCatalogPort the output port implementation
     * @return configured ProductService instance
     */
    @Bean
    public ProductService productService(ProductCatalogPort productCatalogPort) {
        return new ProductService(productCatalogPort);
    }
}
