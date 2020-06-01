package com.myRetail.product.domain;

import com.google.common.base.MoreObjects;
import java.util.Objects;

/**
 * 
 * myRetail Product
 * Returns id, name and currentPrice information
 *
 */
public class Product {

    private String id;

    private String name;

    private Price currentPrice;

    public Product() {
    }

    public Product(String id, String name, Price price) {
        this.id = id;
        this.name = name;
        this.currentPrice = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Price getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Price currentPrice) {
        this.currentPrice = currentPrice;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, currentPrice);
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
        return Objects.equals(id, that.id) && Objects.equals(name, that.name)
                && Objects.equals(currentPrice, that.currentPrice);
    }

    @Override
    public String toString() {
        MoreObjects.ToStringHelper tsh = MoreObjects.toStringHelper(this);
        tsh.omitNullValues();
        tsh.add("id", id);
        tsh.add("name", name);
        tsh.add("currentPrice", currentPrice);
        return tsh.toString();
    }
}
