package com.myRetail.product.catalog.entity;

import com.google.common.base.MoreObjects;
import java.util.Objects;

/**
 * 
 * Target Redsky top-level product object
 *
 */
public class ProductEntity {

    private Product product;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getProductDescription() {
        return getProduct().getItem().getProductDescription().getTitle();
    }

    @Override
    public int hashCode() {
        return Objects.hash(product);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProductEntity that = (ProductEntity) o;
        return Objects.equals(product, that.product);
    }

    @Override
    public String toString() {
        MoreObjects.ToStringHelper tsh = MoreObjects.toStringHelper(this);
        tsh.omitNullValues();
        tsh.add("product", product);
        return tsh.toString();
    }
}
