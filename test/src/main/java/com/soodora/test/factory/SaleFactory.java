package com.soodora.test.factory;

import com.soodora.domain.common.Currency;
import com.soodora.domain.common.MoneyDTO;
import com.soodora.domain.sale.SaleDTO;

import java.math.BigDecimal;
import java.util.Date;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

import static com.soodora.test.factory.ArtistFactory.buildArtistDTO;
import static com.soodora.test.factory.ProductFactory.buildDefaultProduct;
import static com.soodora.test.factory.UserFactory.buildDefaultUser;

@NoArgsConstructor(access = PROTECTED)
public class SaleFactory {

    public static SaleDTO buildSaleDTO() {
        return new SaleDTO(
                "slug",
                buildDefaultProduct(),
                buildArtistDTO("id"),
                buildDefaultUser(),
                10,
                new MoneyDTO(BigDecimal.valueOf(10.99), Currency.EUR, "€"),
                new Date());
    }

}
