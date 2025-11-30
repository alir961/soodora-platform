package com.soodora.domain.product;

import com.soodora.domain.common.Money;
import com.soodora.domain.sale.Sale;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Date;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static java.lang.Math.toIntExact;
import static java.time.Duration.ZERO;
import static java.time.Duration.between;

@Getter
@AllArgsConstructor
public class ProductAnalytics {

    private int currentQuantity;
    private int soldQuantity;
    private int totalRevenue;
    private int salesCount;
    private Duration durationUntilFirstSale;
    private String currency;

    public static ProductAnalytics analyze(Product product) {
        Set<Sale> sales = product.getSales();
        Sale firstSale = product.getFirstSale();
        Duration duration = ZERO;

        if (firstSale != null) {
            Date productCreatedAt = product.getCreatedAt();
            Date firstSaleCreatedAt = firstSale.getCreatedAt();

            duration = between(productCreatedAt.toInstant(), firstSaleCreatedAt.toInstant());
        }
        return new ProductAnalytics(
                toIntExact(product.getQuantity()),
                sales.stream()
                        .map(Sale::getSoldQuantity)
                        .mapToInt(Math::toIntExact)
                        .sum(),
                sales.stream()
                        .map(Sale::getPrice)
                        .map(Money::amount)
                        .map(BigDecimal::longValue)
                        .mapToInt(Math::toIntExact)
                        .sum(),
                sales.size(),
                duration,
                product.getPrice().getSymbol());
    }

}
