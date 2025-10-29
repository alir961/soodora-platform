package com.soodora.infrastructure.sale;

import com.soodora.domain.common.Currency;
import com.soodora.infrastructure.common.MoneyEmbeddable;

import java.math.BigDecimal;
import java.util.Date;

import static java.util.UUID.randomUUID;

import static com.soodora.infrastructure.artist.ArtistEntityFactory.buildDefaultArtistEntity;
import static com.soodora.infrastructure.product.ProductEntityFactory.buildDefaultProductEntity;
import static com.soodora.infrastructure.user.UserEntityFactory.buildDefaultUserEntity;

public class SaleEntityFactory {

    public static SaleEntity buildDefaultSaleEntity() {
        return new SaleEntity(
                randomUUID(),
                "slug",
                buildDefaultProductEntity(buildDefaultArtistEntity()),
                buildDefaultArtistEntity(),
                buildDefaultUserEntity(),
                100,
                new MoneyEmbeddable(BigDecimal.valueOf(100.99), Currency.EUR),
                new Date(),
                new Date());
    }

}
