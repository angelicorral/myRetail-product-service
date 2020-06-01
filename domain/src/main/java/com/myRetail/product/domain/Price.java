package com.myRetail.product.domain;

import com.google.common.base.MoreObjects;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Price of Product
 *
 */
public class Price {

    private BigDecimal value;

    private Currency currency;

    public static Price ofUsd(BigDecimal value) {
        Price price = new Price();
        price.setValue(value);
        price.setCurrency(Currency.USD);
        return price;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, currency);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Price that = (Price) o;
        return Objects.equals(value, that.value) && currency == that.currency;
    }

    @Override
    public String toString() {
        MoreObjects.ToStringHelper tsh = MoreObjects.toStringHelper(this);
        tsh.omitNullValues();
        tsh.add("value", value);
        tsh.add("currency", currency);
        return tsh.toString();
    }
}
