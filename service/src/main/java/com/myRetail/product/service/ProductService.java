package com.myRetail.product.service;

import com.myRetail.product.domain.Product;
import java.math.BigDecimal;
import reactor.core.publisher.Mono;

public interface ProductService {

    Mono<Product> getProduct(String id);

    Mono<Boolean> updateProductPrice(String id, BigDecimal price);
}
