package com.myRetail.product.catalog;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import com.myRetail.product.ProductServiceProperties;
import com.myRetail.product.catalog.entity.ProductEntity;
import com.myRetail.product.test.util.TestUtil;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class RedskyProductRepositoryTest {

    private static final ProductEntity expectedRedskyResponse = TestUtil
            .readObjectFromFile("src/test/resources/redskyResponse.json", ProductEntity.class);

    @Mock
    private WebClient.Builder webClientBuilder;

    private MockWebServer mockServer;

    private ProductRepository productRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        mockServer = new MockWebServer();

        WebClient webClient = WebClient.create(mockServer.url("localhost").toString());
        when(webClientBuilder.baseUrl(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);

        ProductServiceProperties productServiceProperties = new ProductServiceProperties();
        productServiceProperties.setApiReadTimeoutMs(1000);
        productRepository = new RedskyProductRepository(webClientBuilder, productServiceProperties);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockServer.shutdown();
    }

    @Test
    @DisplayName("Given a valid id, when service returns success, then return entity")
    public void testGetProduct_happyPath() throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getClassLoader().getResource("redskyResponse.json").toURI());
        String lines = Files.lines(path).collect(Collectors.joining(""));

        mockServer.enqueue(new MockResponse().setResponseCode(200)
                .setBody(lines)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Mono<ProductEntity> productEntity = productRepository.getProduct("mockId");
        StepVerifier.create(productEntity).expectNext(expectedRedskyResponse).verifyComplete();

    }

    @Test
    @DisplayName("Given a valid id, when service returns 404, then return empty")
    public void testGetProduct_404Received() throws URISyntaxException, IOException {

        mockServer.enqueue(new MockResponse().setResponseCode(404)
                .setBody("404 Error encountered")
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Mono<ProductEntity> productEntity = productRepository.getProduct("mockId");
        StepVerifier.create(productEntity).verifyComplete();

    }

    @Test
    @DisplayName("Given a valid id, when service returns 400, then propagate exception")
    public void testGetProduct_400Received() throws URISyntaxException, IOException {

        mockServer.enqueue(new MockResponse().setResponseCode(400)
                .setBody("400 Error encountered")
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Mono<ProductEntity> productEntity = productRepository.getProduct("mockId");
        StepVerifier.create(productEntity).expectError(WebClientResponseException.class).verify();

    }

    @Test
    @DisplayName("Given a valid id, when service returns 500, then propagate exception")
    public void testGetProduct_500Received() throws URISyntaxException, IOException {

        mockServer.enqueue(new MockResponse().setResponseCode(500)
                .setBody("500 Error encountered")
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Mono<ProductEntity> productEntity = productRepository.getProduct("mockId");
        StepVerifier.create(productEntity).expectError(WebClientResponseException.class).verify();

    }

    @Test
    @DisplayName("Given a valid id, when service takes time to return, then propagate exception")
    public void testGetProduct_requestTimedOut() throws URISyntaxException, IOException {

        mockServer.enqueue(new MockResponse().throttleBody(0, 2, TimeUnit.SECONDS)
                .setResponseCode(500)
                .setBody("500 Error encountered")
                .setSocketPolicy(SocketPolicy.FAIL_HANDSHAKE)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Mono<ProductEntity> productEntity = productRepository.getProduct("mockId");
        StepVerifier.create(productEntity).expectError(TimeoutException.class).verify();

    }
}
