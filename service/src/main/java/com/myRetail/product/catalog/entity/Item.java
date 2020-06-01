package com.myRetail.product.catalog.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import java.util.Objects;

/**
 * Target Redsky Item object
 *
 */
public class Item {

    @JsonProperty("product_description")
    private ProductDescription productDescription;

    public ProductDescription getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(ProductDescription productDescription) {
        this.productDescription = productDescription;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productDescription);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Item that = (Item) o;
        return Objects.equals(productDescription, that.productDescription);
    }

    @Override
    public String toString() {
        MoreObjects.ToStringHelper tsh = MoreObjects.toStringHelper(this);
        tsh.omitNullValues();
        tsh.add("productDescription", productDescription);
        return tsh.toString();
    }
}
