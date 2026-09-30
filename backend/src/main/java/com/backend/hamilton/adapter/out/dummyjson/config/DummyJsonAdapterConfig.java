package com.backend.hamilton.adapter.out.dummyjson.config;

import com.backend.hamilton.adapter.out.dummyjson.DummyJsonProductAdapter;
import com.backend.hamilton.application.port.out.ProductCatalogPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * Configuration for DummyJSON adapter.
 * Configures RestClient and registers the adapter as ProductCatalogPort implementation.
 */
@Configuration
public class DummyJsonAdapterConfig {

    @Value("${dummyjson.api.base-url:https://dummyjson.com}")
    private String baseUrl;

    @Value("${dummyjson.api.timeout:10000}")
    private long timeout;

    /**
     * Creates a RestClient configured for DummyJSON API.
     *
     * @return configured RestClient instance
     */
    @Bean
    public RestClient dummyJsonRestClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Creates the DummyJsonProductAdapter as the ProductCatalogPort implementation.
     *
     * @param restClient configured RestClient for DummyJSON
     * @return adapter instance
     */
    @Bean
    public ProductCatalogPort productCatalogPort(RestClient dummyJsonRestClient) {
        return new DummyJsonProductAdapter(dummyJsonRestClient);
    }
}
