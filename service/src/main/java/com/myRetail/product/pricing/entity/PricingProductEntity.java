package com.myRetail.product.pricing.entity;

import com.google.common.base.MoreObjects;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * 
 * Pricing Repository Entity
 *
 */
public class PricingProductEntity {

    private String id;

    private BigDecimal price;

    public PricingProductEntity(String id, BigDecimal price) {
        this.id = id;
        this.price = price;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PricingProductEntity that = (PricingProductEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(price, that.price);
    }

    @Override
    public String toString() {
        MoreObjects.ToStringHelper tsh = MoreObjects.toStringHelper(this);
        tsh.omitNullValues();
        tsh.add("id", id);
        tsh.add("price", price);
        return tsh.toString();
    }

}
