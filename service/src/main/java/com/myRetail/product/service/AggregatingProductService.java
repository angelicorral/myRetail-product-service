package com.myRetail.product.service;

import com.myRetail.product.catalog.ProductRepository;
import com.myRetail.product.catalog.entity.ProductEntity;
import com.myRetail.product.domain.Price;
import com.myRetail.product.domain.Product;
import com.myRetail.product.pricing.PricingRepository;
import com.myRetail.product.pricing.entity.PricingProductEntity;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 
 * Service that aggregates a Product domain object from different Product sources
 *
 */
@Service
public class AggregatingProductService implements ProductService {

    private final PricingRepository pricingRepository;

    private final ProductRepository productRepository;

    public AggregatingProductService(PricingRepository pricingRepository, ProductRepository productRepository) {
        this.pricingRepository = pricingRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Mono<Product> getProduct(String id) {
        Mono<PricingProductEntity> pricingProduct = pricingRepository.getProduct(id);
        Mono<ProductEntity> catalogProduct = productRepository.getProduct(id);

        return catalogProduct.zipWith(pricingProduct, (product, pricing) -> {
            if (pricing.getPrice() == null) {
                return new Product(id, product.getProductDescription(), null);
            }
            return new Product(id, product.getProductDescription(), Price.ofUsd(pricing.getPrice()));
        });
    }

    @Override
    public Mono<Boolean> updateProductPrice(String id, BigDecimal price) {
        return pricingRepository.updateProductPrice(id, price);
    }

}
