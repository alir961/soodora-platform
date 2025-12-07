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

    public ProductAnalytics append(ProductAnalytics analytics) {
        boolean isBefore = durationUntilFirstSale.toMillis() <= analytics.getDurationUntilFirstSale().toMillis();
        Duration minimumDuration = isBefore ? durationUntilFirstSale : analytics.getDurationUntilFirstSale();

        return new ProductAnalytics(currentQuantity + analytics.getCurrentQuantity(),
                soldQuantity + analytics.getSoldQuantity(),
                totalRevenue + analytics.getTotalRevenue(),
                salesCount + analytics.getSalesCount(),
                minimumDuration,
                currency.isBlank() ? analytics.getCurrency() : currency);
    }

    public static ProductAnalytics analyze(Product product) {
        Set<Sale> sales = product.getSales();

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
                getDurationUntilFirstSale(product),
                product.getPrice().getSymbol());
    }

    public static ProductAnalytics empty() {
        return new ProductAnalytics(0, 0, 0, 0, ZERO, "");
    }

    private static Duration getDurationUntilFirstSale(Product product) {
        Sale firstSale = product.getFirstSale();

        if (firstSale != null) {
            Date productCreatedAt = product.getCreatedAt();
            Date firstSaleCreatedAt = firstSale.getCreatedAt();

            return between(productCreatedAt.toInstant(), firstSaleCreatedAt.toInstant());
        }
        return ZERO;
    }

}
