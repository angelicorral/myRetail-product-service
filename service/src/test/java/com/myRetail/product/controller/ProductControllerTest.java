package com.myRetail.product.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import com.myRetail.product.domain.Currency;
import com.myRetail.product.domain.Price;
import com.myRetail.product.domain.Product;
import com.myRetail.product.service.ProductService;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    private ProductService productService;

    private Product expectedProductAggregate;

    private String mockId = "mockId";

    @Before
    public void setup() {

        expectedProductAggregate = new Product("mockId", "The Big Lebowski (Blu-ray)",
                Price.ofUsd(BigDecimal.valueOf(100)));

        lenient().when(productService.getProduct(eq(mockId))).thenReturn(Mono.just(expectedProductAggregate));
        lenient().when(productService.updateProductPrice(eq(mockId), eq(BigDecimal.valueOf(100))))
                .thenReturn(Mono.just(true));
    }

    @Test
    public void testGetProduct_happyPath() {

        webTestClient.get()
                .uri("/v1/products/mockId")
                .header("Authorization",
                        "Basic " + Base64Utils
                                .encodeToString(("reader" + ":" + "readerPassword").getBytes(StandardCharsets.UTF_8)))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Product.class)
                .isEqualTo(expectedProductAggregate);

    }

    @Test
    public void testGetProduct_notAuthorized() {

        webTestClient.get()
                .uri("/v1/products/mockId")
                .header("Authorization",
                        "Basic " + Base64Utils
                                .encodeToString(("reader" + ":" + "wrongPassword").getBytes(StandardCharsets.UTF_8)))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();

    }

    @Test
    public void testGetProduct_notFoundPath() {

        when(productService.getProduct(eq("unknown"))).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/v1/products/unknown")
                .header("Authorization",
                        "Basic " + Base64Utils
                                .encodeToString(("reader" + ":" + "readerPassword").getBytes(StandardCharsets.UTF_8)))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();

    }

    @Test
    public void testGetProduct_timeoutExceptionPath() {

        when(productService.getProduct(eq("mockId"))).thenReturn(Mono.error(new TimeoutException()));

        webTestClient.get()
                .uri("/v1/products/mockId")
                .header("Authorization",
                        "Basic " + Base64Utils
                                .encodeToString(("reader" + ":" + "readerPassword").getBytes(StandardCharsets.UTF_8)))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .is5xxServerError();

    }

    @Test
    public void testGetProduct_otherExceptionPath() {

        when(productService.getProduct(eq("mockId"))).thenReturn(Mono.error(new WebClientResponseException(500,
                "Internal Server Error", HttpHeaders.EMPTY, "Message Body".getBytes(), StandardCharsets.UTF_8)));

        webTestClient.get()
                .uri("/v1/products/mockId")
                .header("Authorization",
                        "Basic " + Base64Utils
                                .encodeToString(("reader" + ":" + "readerPassword").getBytes(StandardCharsets.UTF_8)))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .is5xxServerError();

    }

    @Test
    public void testUpdateProduct_happyPath() {

        Product productInput = new Product("mockId", "any name", Price.ofUsd(BigDecimal.valueOf(100)));
        
        webTestClient.put()
                .uri("/v1/products/mockId")
                .header("Authorization",
                        "Basic " + Base64Utils
                                .encodeToString(("admin" + ":" + "adminPassword").getBytes(StandardCharsets.UTF_8)))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(productInput))
                .exchange()
                .expectStatus()
                .isOk();

    }

    @Test
    public void testUpdateProduct_forbiddenAccess() {

        Product productInput = new Product("mockId", "any name", Price.ofUsd(BigDecimal.valueOf(100)));
        
        webTestClient.put()
                .uri("/v1/products/mockId")
                .header("Authorization",
                        "Basic " + Base64Utils
                                .encodeToString(("reader" + ":" + "readerPassword").getBytes(StandardCharsets.UTF_8)))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(productInput))
                .exchange()
                .expectStatus()
                .isForbidden();

    }

    @Test
    public void testUpdateProduct_differentId() {

        Product productInput = new Product("differentId", "any name", Price.ofUsd(BigDecimal.valueOf(100)));
        webTestClient.put()
                .uri("/v1/products/mockId")
                .header("Authorization",
                        "Basic " + Base64Utils
                                .encodeToString(("admin" + ":" + "adminPassword").getBytes(StandardCharsets.UTF_8)))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(productInput))
                .exchange()
                .expectStatus()
                .isBadRequest();

    }

    @Test
    public void testUpdateProduct_invalidPriceZeroValue() {

        Product productInput = new Product("mockId", "any name", Price.ofUsd(BigDecimal.valueOf(0)));
        webTestClient.put()
                .uri("/v1/products/mockId")
                .header("Authorization",
                        "Basic " + Base64Utils
                                .encodeToString(("admin" + ":" + "adminPassword").getBytes(StandardCharsets.UTF_8)))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(productInput))
                .exchange()
                .expectStatus()
                .isBadRequest();

    }

    @Test
    public void testUpdateProduct_invalidPriceNonUsd() {

        Price invalidPrice = new Price();
        invalidPrice.setCurrency(Currency.AUD);
        invalidPrice.setValue(BigDecimal.valueOf(100));

        Product productInput = new Product("mockId", "any name", invalidPrice);
        webTestClient.put()
                .uri("/v1/products/mockId")
                .header("Authorization",
                        "Basic " + Base64Utils
                                .encodeToString(("admin" + ":" + "adminPassword").getBytes(StandardCharsets.UTF_8)))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(productInput))
                .exchange()
                .expectStatus()
                .isBadRequest();

    }

}
