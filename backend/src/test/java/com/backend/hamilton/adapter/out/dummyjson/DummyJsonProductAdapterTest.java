package com.backend.hamilton.adapter.out.dummyjson;

import com.backend.hamilton.domain.exception.ExternalServiceException;
import com.backend.hamilton.domain.exception.ExternalServiceTimeoutException;
import com.backend.hamilton.domain.exception.ProductNotFoundException;
import com.backend.hamilton.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * Unit tests for DummyJsonProductAdapter using MockRestServiceServer.
 */
class DummyJsonProductAdapterTest {

    private static final String BASE_URL = "https://dummyjson.com";

    private DummyJsonProductAdapter adapter;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        mockServer = MockRestServiceServer.bindTo(builder).build();
        RestClient restClient = builder.build();
        adapter = new DummyJsonProductAdapter(restClient);
    }

    @Test
    void shouldGetAllProductsWithLimitZero() {
        String responseBody = """
                {
                    "products": [
                        {
                            "id": 1,
                            "title": "Product 1",
                            "description": "Description 1",
                            "price": 99.99,
                            "rating": 4.5,
                            "thumbnail": "thumb1.jpg",
                            "images": ["image1.jpg"],
                            "category": "electronics",
                            "brand": "Brand 1"
                        },
                        {
                            "id": 2,
                            "title": "Product 2",
                            "description": "Description 2",
                            "price": 49.99,
                            "rating": 3.8,
                            "thumbnail": "thumb2.jpg",
                            "images": ["image2.jpg"],
                            "category": "beauty",
                            "brand": "Brand 2"
                        }
                    ],
                    "total": 2,
                    "skip": 0,
                    "limit": 0
                }
                """;

        mockServer.expect(requestTo(BASE_URL + "/products?limit=0"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(queryParam("limit", "0"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        List<Product> products = adapter.findAll();

        assertEquals(2, products.size());
        assertEquals(1L, products.get(0).id());
        assertEquals("Product 1", products.get(0).title());
        assertEquals(2L, products.get(1).id());
        assertEquals("Product 2", products.get(1).title());
        mockServer.verify();
    }

    @Test
    void shouldGetProductById() {
        String responseBody = """
                {
                    "id": 1,
                    "title": "Test Product",
                    "description": "Test Description",
                    "price": 99.99,
                    "rating": 4.5,
                    "thumbnail": "thumbnail.jpg",
                    "images": ["image1.jpg", "image2.jpg"],
                    "category": "electronics",
                    "brand": "Test Brand"
                }
                """;

        mockServer.expect(requestTo(BASE_URL + "/products/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        Product product = adapter.findById(1L);

        assertEquals(1L, product.id());
        assertEquals("Test Product", product.title());
        assertEquals("Test Description", product.description());
        assertEquals(BigDecimal.valueOf(99.99), product.price());
        assertEquals(BigDecimal.valueOf(4.5), product.rating());
        assertEquals("thumbnail.jpg", product.thumbnail());
        assertEquals(2, product.images().size());
        assertEquals("electronics", product.category());
        assertEquals("Test Brand", product.brand());
        mockServer.verify();
    }

    @Test
    void shouldMapListCorrectly() {
        String responseBody = """
                {
                    "products": [
                        {
                            "id": 10,
                            "title": "Product A",
                            "description": "Description A",
                            "price": 10.00,
                            "rating": 5.0,
                            "thumbnail": "thumbA.jpg",
                            "images": ["imageA1.jpg", "imageA2.jpg", "imageA3.jpg"],
                            "category": "furniture",
                            "brand": "Brand A"
                        }
                    ],
                    "total": 1,
                    "skip": 0,
                    "limit": 0
                }
                """;

        mockServer.expect(requestTo(BASE_URL + "/products?limit=0"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        List<Product> products = adapter.findAll();

        assertEquals(1, products.size());
        Product product = products.get(0);
        assertEquals(10L, product.id());
        assertEquals("Product A", product.title());
        assertEquals("Description A", product.description());
        assertEquals(0, BigDecimal.valueOf(10.00).compareTo(product.price()));
        assertEquals(0, BigDecimal.valueOf(5.0).compareTo(product.rating()));
        assertEquals("thumbA.jpg", product.thumbnail());
        assertEquals(3, product.images().size());
        assertEquals("furniture", product.category());
        assertEquals("Brand A", product.brand());
        mockServer.verify();
    }

    @Test
    void shouldMapDetailCorrectly() {
        String responseBody = """
                {
                    "id": 42,
                    "title": "Detailed Product",
                    "description": "Detailed Description",
                    "price": 123.45,
                    "rating": 4.2,
                    "thumbnail": "detail_thumb.jpg",
                    "images": ["detail1.jpg"],
                    "category": "sports",
                    "brand": "Sports Brand"
                }
                """;

        mockServer.expect(requestTo(BASE_URL + "/products/42"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        Product product = adapter.findById(42L);

        assertEquals(42L, product.id());
        assertEquals("Detailed Product", product.title());
        assertEquals("Detailed Description", product.description());
        assertEquals(BigDecimal.valueOf(123.45), product.price());
        assertEquals(BigDecimal.valueOf(4.2), product.rating());
        assertEquals("detail_thumb.jpg", product.thumbnail());
        assertEquals(1, product.images().size());
        assertEquals("sports", product.category());
        assertEquals("Sports Brand", product.brand());
        mockServer.verify();
    }

    @Test
    void shouldThrowProductNotFoundExceptionOn404() {
        mockServer.expect(requestTo(BASE_URL + "/products/999"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> adapter.findById(999L)
        );

        assertTrue(exception.getMessage().contains("999"));
        mockServer.verify();
    }

    @Test
    void shouldThrowExternalServiceExceptionOn500() {
        mockServer.expect(requestTo(BASE_URL + "/products?limit=0"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        ExternalServiceException exception = assertThrows(
                ExternalServiceException.class,
                () -> adapter.findAll()
        );

        assertTrue(exception.getMessage().contains("Server error"));
        mockServer.verify();
    }

    @Test
    void shouldThrowExternalServiceExceptionOnEmptyBody() {
        mockServer.expect(requestTo(BASE_URL + "/products/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("", MediaType.APPLICATION_JSON));

        ExternalServiceException exception = assertThrows(
                ExternalServiceException.class,
                () -> adapter.findById(1L)
        );

        assertTrue(exception.getMessage().contains("Empty response") ||
                   exception.getMessage().contains("Deserialization"));
        mockServer.verify();
    }

    @Test
    void shouldThrowExternalServiceTimeoutExceptionOnTimeout() {
        mockServer.expect(requestTo(BASE_URL + "/products/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(request -> {
                    throw new SocketTimeoutException("Read timed out");
                });

        ExternalServiceTimeoutException exception = assertThrows(
                ExternalServiceTimeoutException.class,
                () -> adapter.findById(1L)
        );

        assertTrue(exception.getMessage().contains("Timeout"));
        mockServer.verify();
    }

    @Test
    void shouldHandleNullImages() {
        String responseBody = """
                {
                    "id": 1,
                    "title": "Product Without Images",
                    "description": "Description",
                    "price": 50.00,
                    "rating": 3.5,
                    "thumbnail": "thumbnail.jpg",
                    "images": null,
                    "category": "misc",
                    "brand": "Brand"
                }
                """;

        mockServer.expect(requestTo(BASE_URL + "/products/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        Product product = adapter.findById(1L);

        assertNotNull(product.images());
        assertTrue(product.images().isEmpty());
        mockServer.verify();
    }

    @Test
    void shouldHandleNullBrand() {
        String responseBody = """
                {
                    "id": 1,
                    "title": "Product Without Brand",
                    "description": "Description",
                    "price": 25.00,
                    "rating": 4.0,
                    "thumbnail": "thumbnail.jpg",
                    "images": ["image1.jpg"],
                    "category": "generic",
                    "brand": null
                }
                """;

        mockServer.expect(requestTo(BASE_URL + "/products/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        Product product = adapter.findById(1L);

        assertNull(product.brand());
        mockServer.verify();
    }

    @Test
    void shouldVerifyLimitZeroParameterIsPresent() {
        String responseBody = """
                {
                    "products": [],
                    "total": 0,
                    "skip": 0,
                    "limit": 0
                }
                """;

        mockServer.expect(requestTo(BASE_URL + "/products?limit=0"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(queryParam("limit", "0"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        adapter.findAll();

        mockServer.verify();
    }

    @Test
    void shouldHandleEmptyProductsList() {
        String responseBody = """
                {
                    "products": [],
                    "total": 0,
                    "skip": 0,
                    "limit": 0
                }
                """;

        mockServer.expect(requestTo(BASE_URL + "/products?limit=0"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        List<Product> products = adapter.findAll();

        assertTrue(products.isEmpty());
        mockServer.verify();
    }

    @Test
    void shouldThrowExternalServiceExceptionOnBadRequest() {
        mockServer.expect(requestTo(BASE_URL + "/products/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        ExternalServiceException exception = assertThrows(
                ExternalServiceException.class,
                () -> adapter.findById(1L)
        );

        assertTrue(exception.getMessage().contains("Client error"));
        mockServer.verify();
    }
}
