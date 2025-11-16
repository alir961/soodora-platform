package com.soodora.domain.product;

import java.time.Duration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductAnalytics {

    private int currentQuantity;
    private int soldQuantity;
    private int totalRevenue;
    private int salesCount;
    private Duration durationUntilFirstSale;
    private String currency;

}
