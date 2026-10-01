package com.backend.hamilton.adapter.out.dummyjson;

import com.backend.hamilton.application.port.out.ProductCatalogPort;
import com.backend.hamilton.configuration.properties.DummyJsonClientProperties;
import com.backend.hamilton.domain.exception.ErrorCode;
import com.backend.hamilton.domain.exception.ExternalServiceException;
import com.backend.hamilton.domain.exception.ExternalServiceTimeoutException;
import com.backend.hamilton.domain.exception.ProductNotFoundException;
import com.backend.hamilton.domain.model.Product;
import com.backend.hamilton.domain.model.ProductQuery;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Adapter for accessing DummyJSON product catalog.
 * Implements the output port using RestClient to communicate with the external API.
 */
public class DummyJsonProductAdapter implements ProductCatalogPort {

    private final RestClient restClient;
    private final DummyJsonClientProperties properties;

    /**
     * Constructor with dependency injection.
     *
     * @param restClient configured RestClient for DummyJSON API
     * @param properties endpoints, query defaults and timeouts of the DummyJSON API
     */
    public DummyJsonProductAdapter(RestClient restClient, DummyJsonClientProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    @Override
    public List<Product> findAll() {
        return findAll(new ProductQuery(0, 0, null, null));
    }

    @Override
    public List<Product> findAll(ProductQuery query) {
        try {
            String uri = DummyJsonUriBuilder.build(
                    properties.baseUrl(),
                    properties.paths().products(),
                    properties.query().allProductsLimit(),
                    query);

            DummyJsonProductsResponse response = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(DummyJsonProductsResponse.class);

            if (response == null || response.products() == null) {
                throw new ExternalServiceException(ErrorCode.EXTERNAL_SERVICE_ERROR, Map.of("reason", "empty-body"));
            }

            return response.products().stream()
                    .map(DummyJsonProductMapper::toDomain)
                    .collect(Collectors.toList());

        } catch (HttpClientErrorException.NotFound e) {
            throw new ProductNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, Map.of("resource", "products"), e);
        } catch (HttpClientErrorException e) {
            throw new ExternalServiceException(
                    ErrorCode.EXTERNAL_SERVICE_ERROR, Map.of("status", String.valueOf(e.getStatusCode())), e);
        } catch (HttpServerErrorException e) {
            throw new ExternalServiceException(
                    ErrorCode.EXTERNAL_SERVICE_ERROR, Map.of("status", String.valueOf(e.getStatusCode())), e);
        } catch (ResourceAccessException e) {
            throw new ExternalServiceTimeoutException(ErrorCode.EXTERNAL_SERVICE_TIMEOUT, Map.of(), e);
        } catch (Exception e) {
            throw new ExternalServiceException(
                    ErrorCode.EXTERNAL_SERVICE_ERROR, Map.of("reason", "unexpected"), e);
        }
    }

    @Override
    public Product findById(Long id) {
        try {
            DummyJsonProductDto dto = restClient.get()
                    .uri(properties.productByIdTemplate(), id)
                    .retrieve()
                    .body(DummyJsonProductDto.class);

            if (dto == null) {
                throw new ExternalServiceException(ErrorCode.EXTERNAL_SERVICE_ERROR, Map.of("id", String.valueOf(id)));
            }

            return DummyJsonProductMapper.toDomain(dto);

        } catch (HttpClientErrorException.NotFound e) {
            throw new ProductNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, Map.of("id", String.valueOf(id)), e);
        } catch (HttpClientErrorException e) {
            throw new ExternalServiceException(
                    ErrorCode.EXTERNAL_SERVICE_ERROR, Map.of("status", String.valueOf(e.getStatusCode())), e);
        } catch (HttpServerErrorException e) {
            throw new ExternalServiceException(
                    ErrorCode.EXTERNAL_SERVICE_ERROR, Map.of("status", String.valueOf(e.getStatusCode())), e);
        } catch (ResourceAccessException e) {
            throw new ExternalServiceTimeoutException(ErrorCode.EXTERNAL_SERVICE_TIMEOUT, Map.of(), e);
        } catch (Exception e) {
            throw new ExternalServiceException(
                    ErrorCode.EXTERNAL_SERVICE_ERROR, Map.of("reason", "unexpected"), e);
        }
    }
}