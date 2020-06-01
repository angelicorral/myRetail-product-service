package com.myRetail.product.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import com.datastax.oss.driver.api.core.connection.ClosedConnectionException;
import com.myRetail.product.catalog.ProductRepository;
import com.myRetail.product.catalog.entity.ProductEntity;
import com.myRetail.product.domain.Price;
import com.myRetail.product.domain.Product;
import com.myRetail.product.pricing.PricingRepository;
import com.myRetail.product.pricing.entity.PricingProductEntity;
import com.myRetail.product.test.util.TestUtil;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class AggregatingProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PricingRepository pricingRepository;

    private ProductService aggregatingProductService;

    private static final ProductEntity mockProductEntity = TestUtil
            .readObjectFromFile("src/test/resources/redskyResponse.json", ProductEntity.class);

    private PricingProductEntity mockPricingProductEntity;

    private Product expectedProductAggregate;

    private String mockId = "mockId";

    @BeforeEach
    public void setup() {
        aggregatingProductService = new AggregatingProductService(pricingRepository, productRepository);

        expectedProductAggregate = new Product("mockId", "The Big Lebowski (Blu-ray)",
                Price.ofUsd(BigDecimal.valueOf(100)));
        mockPricingProductEntity = new PricingProductEntity("mockId", BigDecimal.valueOf(100));

        lenient().when(productRepository.getProduct(eq(mockId))).thenReturn(Mono.just(mockProductEntity));
        lenient().when(pricingRepository.getProduct(eq(mockId))).thenReturn(Mono.just(mockPricingProductEntity));
    }

    @Test
    @DisplayName("Given valid id, when both repositories return entities, then return aggregated product")
    public void testGetProduct_happyPath() {
        Mono<Product> productMono = aggregatingProductService.getProduct("mockId");

        StepVerifier.create(productMono).expectNext(expectedProductAggregate).verifyComplete();
    }

    @Test
    @DisplayName("Given valid id, when both repositories return entities and price is null, then return aggregated product with null price")
    public void testGetProduct_nullPrice() {
        mockPricingProductEntity = new PricingProductEntity("mockId", null);
        when(pricingRepository.getProduct(eq(mockId))).thenReturn(Mono.just(mockPricingProductEntity));

        Mono<Product> productMono = aggregatingProductService.getProduct("mockId");

        StepVerifier.create(productMono)
                .expectNext(new Product("mockId", "The Big Lebowski (Blu-ray)", null))
                .verifyComplete();
    }

    @Test
    @DisplayName("Given valid id, when product repository return exception, then propagate exception")
    public void testGetProduct_productRepositoryClientException() {
        when(productRepository.getProduct(eq(mockId))).thenReturn(Mono.error(new WebClientResponseException(500,
                "Internal Server Error", HttpHeaders.EMPTY, "Message Body".getBytes(), StandardCharsets.UTF_8)));

        Mono<Product> productMono = aggregatingProductService.getProduct("mockId");

        StepVerifier.create(productMono).expectError(WebClientResponseException.class).verify();
    }

    @Test
    @DisplayName("Given valid id, when pricing repository return exception, then propagate exception")
    public void testGetProduct_pricingRepositoryClientException() {
        when(pricingRepository.getProduct(eq(mockId))).thenReturn(Mono.error(new WebClientResponseException(500,
                "Internal Server Error", HttpHeaders.EMPTY, "Message Body".getBytes(), StandardCharsets.UTF_8)));

        Mono<Product> productMono = aggregatingProductService.getProduct("mockId");

        StepVerifier.create(productMono).expectError(WebClientResponseException.class).verify();
    }

    @Test
    @DisplayName("Given valid id, when both pricing repository returns timeout exception, then propagate exception")
    public void testGetProduct_pricingRepositoryException() {
        when(pricingRepository.getProduct(eq(mockId))).thenReturn(Mono.error(new TimeoutException()));

        Mono<Product> productMono = aggregatingProductService.getProduct("mockId");

        StepVerifier.create(productMono).expectError(TimeoutException.class).verify();
    }

    @Test
    @DisplayName("Given valid id, when both product repository returns exception, then propagate exception")
    public void testGetProduct_productRepositoryException() {
        when(productRepository.getProduct(eq(mockId))).thenReturn(Mono.error(new TimeoutException()));

        Mono<Product> productMono = aggregatingProductService.getProduct("mockId");

        StepVerifier.create(productMono).expectError(TimeoutException.class).verify();
    }

    @Test
    @DisplayName("Given valid id and price, when both pricing repository called with no exception, then return result")
    public void testUpdateProduct_happyPath() {
        when(pricingRepository.updateProductPrice(eq(mockId), eq(BigDecimal.valueOf(100)))).thenReturn(Mono.just(true));

        Mono<Boolean> resultMono = aggregatingProductService.updateProductPrice("mockId", BigDecimal.valueOf(100));

        StepVerifier.create(resultMono).expectNext(true).verifyComplete();
    }

    @Test
    @DisplayName("Given valid id and price, when both pricing repository returns exception, then propagate exception")
    public void testUpdateProduct_exceptionPath() {
        when(pricingRepository.updateProductPrice(eq(mockId), eq(BigDecimal.valueOf(100))))
                .thenReturn(Mono.error(new ClosedConnectionException("closed")));

        Mono<Boolean> resultMono = aggregatingProductService.updateProductPrice("mockId", BigDecimal.valueOf(100));

        StepVerifier.create(resultMono).expectError(ClosedConnectionException.class).verify();
    }

}
