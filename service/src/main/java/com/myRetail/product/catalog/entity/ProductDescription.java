package com.myRetail.product.catalog.entity;

import com.google.common.base.MoreObjects;
import java.util.Objects;

/**
 * 
 * Target Redsky Product Description
 * Contains title which is considered the product name
 *
 */
public class ProductDescription {

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProductDescription that = (ProductDescription) o;
        return Objects.equals(title, that.title);
    }

    @Override
    public String toString() {
        MoreObjects.ToStringHelper tsh = MoreObjects.toStringHelper(this);
        tsh.omitNullValues();
        tsh.add("title", title);
        return tsh.toString();
    }
}
