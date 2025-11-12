package com.soodora.test.factory;

import com.soodora.domain.common.Currency;
import com.soodora.domain.common.Money;
import com.soodora.domain.sale.SaleDTO;
import com.soodora.domain.user.PublicUserDTO;

import java.math.BigDecimal;
import java.util.Date;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

import static com.soodora.test.TestConstants.USER;
import static com.soodora.test.factory.ArtistFactory.buildDefaultArtist;
import static com.soodora.test.factory.ProductFactory.buildDefaultProduct;

@NoArgsConstructor(access = PROTECTED)
public class SaleFactory {

    public static SaleDTO buildSaleDTO() {
        return new SaleDTO(
                "slug",
                buildDefaultProduct(),
                buildDefaultArtist("id"),
                PublicUserDTO.build(USER),
                10,
                Money.of(BigDecimal.valueOf(10.99), Currency.EUR),
                new Date());
    }

}
