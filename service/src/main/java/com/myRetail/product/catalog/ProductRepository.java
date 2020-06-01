package com.myRetail.product.catalog;

import com.myRetail.product.catalog.entity.ProductEntity;
import reactor.core.publisher.Mono;

public interface ProductRepository {

    Mono<ProductEntity> getProduct(String id);
}
