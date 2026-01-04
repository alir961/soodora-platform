package com.soodora.api.indicator;

import com.soodora.domain.indicator.ProductIndicator;

public record ProductIndicatorDTO(
        int products,
        int productsOutOfStock,
        int productsWithoutSale,
        int totalRevenue,
        int totalStock,
        double meanProductPrice,
        double meanRevenuePerArtist,
        double meanProductSoldPerArtist,
        String currency) {

    public static ProductIndicatorDTO build(ProductIndicator indicator) {
        return new ProductIndicatorDTO(
                indicator.getProducts(),
                indicator.getProductsOutOfStock(),
                indicator.getProductsWithoutSale(),
                indicator.getTotalRevenue(),
                indicator.getTotalStock(),
                indicator.getMeanProductPrice(),
                indicator.getMeanRevenuePerArtist(),
                indicator.getMeanProductSoldPerArtist(),
                indicator.getCurrency().getSymbol());
    }

}
