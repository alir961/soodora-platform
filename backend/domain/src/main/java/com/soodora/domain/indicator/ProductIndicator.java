package com.soodora.domain.indicator;

import com.soodora.domain.common.Currency;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductIndicator {

    private int products;
    private int productsOutOfStock;
    private int productsWithoutSale;
    private int totalRevenue;
    private int totalStock;
    private double meanProductPrice;
    private double meanRevenuePerArtist;
    private double meanProductSoldPerArtist;
    private Currency currency;

}
