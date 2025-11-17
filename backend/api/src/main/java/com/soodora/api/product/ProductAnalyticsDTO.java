package com.soodora.api.product;

import com.soodora.api.common.JsonResponse;
import com.soodora.domain.product.ProductAnalytics;

public record ProductAnalyticsDTO(
        int currentQuantity,
        int soldQuantity,
        int totalRevenue,
        int salesCount,
        long durationUntilFirstSale,
        String currency) implements JsonResponse {

    public static ProductAnalyticsDTO build(ProductAnalytics analytics) {
        return new ProductAnalyticsDTO(
                analytics.getCurrentQuantity(),
                analytics.getSoldQuantity(),
                analytics.getTotalRevenue(),
                analytics.getSalesCount(),
                analytics.getDurationUntilFirstSale().toMillis(),
                analytics.getCurrency());
    }

}
