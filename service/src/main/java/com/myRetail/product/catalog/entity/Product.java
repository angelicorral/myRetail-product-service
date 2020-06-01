package com.myRetail.product.catalog.entity;

import com.google.common.base.MoreObjects;
import java.util.Objects;

/**
 * 
 * Target Redsky Product Object
 *
 */
public class Product {

    private Item item;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public int hashCode() {
        return Objects.hash(item);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Product that = (Product) o;
        return Objects.equals(item, that.item);
    }

    @Override
    public String toString() {
        MoreObjects.ToStringHelper tsh = MoreObjects.toStringHelper(this);
        tsh.omitNullValues();
        tsh.add("item", item);
        return tsh.toString();
    }
}
