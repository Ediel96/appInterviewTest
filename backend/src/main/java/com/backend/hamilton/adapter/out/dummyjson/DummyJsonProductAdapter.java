package com.backend.hamilton.adapter.out.dummyjson;

import com.backend.hamilton.application.port.out.ProductCatalogPort;
import com.backend.hamilton.domain.exception.ExternalServiceException;
import com.backend.hamilton.domain.exception.ExternalServiceTimeoutException;
import com.backend.hamilton.domain.exception.ProductNotFoundException;
import com.backend.hamilton.domain.model.Product;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Adapter for accessing DummyJSON product catalog.
 * Implements the output port using RestClient to communicate with the external API.
 */
public class DummyJsonProductAdapter implements ProductCatalogPort {

    private final RestClient restClient;

    /**
     * Constructor with dependency injection.
     *
     * @param restClient configured RestClient for DummyJSON API
     */
    public DummyJsonProductAdapter(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<Product> findAll() {
        try {
            DummyJsonProductsResponse response = restClient.get()
                    .uri("/products?limit=0")
                    .retrieve()
                    .body(DummyJsonProductsResponse.class);

            if (response == null || response.products() == null) {
                throw new ExternalServiceException("Empty response body from DummyJSON API");
            }

            return response.products().stream()
                    .map(DummyJsonProductMapper::toDomain)
                    .collect(Collectors.toList());

        } catch (HttpClientErrorException.NotFound e) {
            throw new ProductNotFoundException("Products endpoint not found");
        } catch (HttpClientErrorException e) {
            throw new ExternalServiceException("Client error from DummyJSON API: " + e.getStatusCode(), e);
        } catch (HttpServerErrorException e) {
            throw new ExternalServiceException("Server error from DummyJSON API: " + e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            throw new ExternalServiceTimeoutException("Timeout accessing DummyJSON API", e);
        } catch (Exception e) {
            throw new ExternalServiceException("Deserialization or unexpected error from DummyJSON API", e);
        }
    }

    @Override
    public Product findById(Long id) {
        try {
            DummyJsonProductDto dto = restClient.get()
                    .uri("/products/{id}", id)
                    .retrieve()
                    .body(DummyJsonProductDto.class);

            if (dto == null) {
                throw new ExternalServiceException("Empty response body from DummyJSON API");
            }

            return DummyJsonProductMapper.toDomain(dto);

        } catch (HttpClientErrorException.NotFound e) {
            throw new ProductNotFoundException("Product with ID " + id + " not found");
        } catch (HttpClientErrorException e) {
            throw new ExternalServiceException("Client error from DummyJSON API: " + e.getStatusCode(), e);
        } catch (HttpServerErrorException e) {
            throw new ExternalServiceException("Server error from DummyJSON API: " + e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            throw new ExternalServiceTimeoutException("Timeout accessing DummyJSON API", e);
        } catch (Exception e) {
            throw new ExternalServiceException("Deserialization or unexpected error from DummyJSON API", e);
        }
    }
}
