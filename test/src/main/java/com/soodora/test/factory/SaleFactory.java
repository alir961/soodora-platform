package com.soodora.test.factory;

import com.soodora.domain.common.Currency;
import com.soodora.domain.common.Money;
import com.soodora.domain.sale.Sale;

import java.math.BigDecimal;
import java.util.Date;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

import static com.soodora.test.TestConstants.ARTIST_USER;
import static com.soodora.test.TestConstants.USER;

@NoArgsConstructor(access = PROTECTED)
public class SaleFactory {

    public static Sale buildDefaultSale() {
        return new Sale(
                "slug",
                ARTIST_USER,
                USER,
                10,
                Money.of(BigDecimal.valueOf(10.99), Currency.EUR),
                new Date(),
                new Date());
    }

}
