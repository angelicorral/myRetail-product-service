package com.myRetail.product.catalog;

import com.myRetail.product.ProductServiceProperties;
import com.myRetail.product.catalog.entity.ProductEntity;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Repository that houses the webclient for Target Redsky API
 * Read and connect timeout values are configurable through properties
 */
@Component
public class RedskyProductRepository implements ProductRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedskyProductRepository.class);

    private final WebClient webClient;

    private final ProductServiceProperties properties;

    public RedskyProductRepository(WebClient.Builder webClientBuilder, ProductServiceProperties properties) {
        this.webClient = webClientBuilder.baseUrl("https://redsky.target.com").build();
        this.properties = properties;
    }

    @Override
    public Mono<ProductEntity> getProduct(String id) {
        return webClient.get()
                .uri("/v2/pdp/tcin/{id}", id)
                .retrieve()
                .bodyToMono(ProductEntity.class)
                .timeout(Duration.ofMillis(properties.getApiReadTimeoutMs()))
                .onErrorResume(WebClientResponseException.class, e -> {
                    LOGGER.error("Exception encountered calling redsky for id {}", id, e);
                    if (e.getRawStatusCode() == HttpStatus.NOT_FOUND.value()) {
                        return Mono.empty();
                    }
                    return Mono.error(e);
                })
                .onErrorResume(e -> {
                    LOGGER.error("Exception encountered calling redsky for id {}", id, e);
                    return Mono.error(e);
                });
    }

}
