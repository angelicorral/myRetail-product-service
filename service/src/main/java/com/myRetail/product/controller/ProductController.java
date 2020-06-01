package com.myRetail.product.controller;

import com.myRetail.product.domain.Product;
import com.myRetail.product.pricing.ProductValidator;
import com.myRetail.product.service.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/v1/products", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public Mono<Product> getProduct(@PathVariable String id) {

        // sanitize input
        String trimmedId = StringUtils.trimToEmpty(id);
        return productService.getProduct(trimmedId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")));
    }

    @PutMapping("/{id}")
    public Mono<String> updateProduct(@PathVariable String id, @RequestBody Product product) {

        // sanitize input
        String trimmedId = StringUtils.trimToEmpty(id);

        // validate if id of product is same
        if (!ProductValidator.isValidProduct(trimmedId, product)) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid Product Object, ensure id and price are correct"));
        }
        return productService.updateProductPrice(trimmedId, product.getCurrentPrice().getValue()).flatMap(success -> {
            if (!success) {
                return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No record found to update"));
            }
            return Mono.just("Updated");
        });
    }

}
