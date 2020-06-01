package com.myRetail.product.pricing;

import com.myRetail.product.pricing.entity.PricingProductEntity;
import java.math.BigDecimal;
import reactor.core.publisher.Mono;

public interface PricingRepository {

    Mono<PricingProductEntity> getProduct(String id);
    
    Mono<Boolean> updateProductPrice(String id, BigDecimal price);
}
