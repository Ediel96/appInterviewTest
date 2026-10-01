package com.backend.hamilton.adapter.out.dummyjson.config;

import com.backend.hamilton.adapter.out.dummyjson.DummyJsonProductAdapter;
import com.backend.hamilton.application.port.out.ProductCatalogPort;
import com.backend.hamilton.configuration.properties.DummyJsonClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Configuration for DummyJSON adapter.
 * Configures RestClient and registers the adapter as ProductCatalogPort implementation.
 */
@Configuration
public class DummyJsonAdapterConfig {

    /**
     * Creates a RestClient configured for the DummyJSON API, applying the configured
     * connect and read timeouts.
     *
     * @param properties DummyJSON endpoints, query defaults and timeouts
     * @return configured RestClient instance
     */
    @Bean
    public RestClient dummyJsonRestClient(DummyJsonClientProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.timeouts().connect());
        requestFactory.setReadTimeout(properties.timeouts().read());

        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(requestFactory)
                .build();
    }

    /**
     * Creates the DummyJsonProductAdapter as the ProductCatalogPort implementation.
     *
     * @param restClient configured RestClient for DummyJSON
     * @param properties DummyJSON endpoints, query defaults and timeouts
     * @return adapter instance
     */
    @Bean
    public ProductCatalogPort productCatalogPort(
            RestClient dummyJsonRestClient,
            DummyJsonClientProperties properties) {
        return new DummyJsonProductAdapter(dummyJsonRestClient, properties);
    }
}