package com.myRetail.product.pricing;

import com.myRetail.product.domain.Currency;
import com.myRetail.product.domain.Product;
import java.math.BigDecimal;
import java.util.function.Predicate;

public final class ProductValidator {

    private static Predicate<Product> isValidPriceValues = (p) -> {
        return p.getCurrentPrice().getValue() != null && BigDecimal.ZERO.compareTo(p.getCurrentPrice().getValue()) != 0
                && p.getCurrentPrice().getCurrency() == Currency.USD;
    };

    private static Predicate<Product> isPriceNotNull = (p) -> {
        return p.getCurrentPrice() != null;
    };

    private static Predicate<Product> isValidPrice = (p) -> {
        return isPriceNotNull.test(p) && isValidPriceValues.test(p);
    };

    /**
     * This product validator just validates the following
     * - id is same as path variable
     * - currency is in USD
     * - value is greater than 0
     * 
     * @param id
     * @param product
     * @return
     */
    public static boolean isValidProduct(String id, Product product) {
        // just check if id is the same. Ignore changes to name

        if (!id.equals(product.getId())) {
            return false;
        }

        // check that price is not null and greater than 0
        if (!isValidPrice.test(product)) {
            return false;
        }

        return true;
    }

}
