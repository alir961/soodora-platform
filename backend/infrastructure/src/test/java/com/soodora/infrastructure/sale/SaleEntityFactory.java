package com.soodora.infrastructure.sale;

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
                new BigDecimal(10),
                new Date(),
                new Date());
    }

}
