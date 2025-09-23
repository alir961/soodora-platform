package com.soodora.infrastructure.product;

import com.soodora.domain.common.Currency;
import com.soodora.infrastructure.artist.ArtistEntity;
import com.soodora.infrastructure.common.Money;
import com.soodora.test.factory.ProductFactory;

import java.util.Date;

import static java.util.UUID.randomUUID;

public class ProductEntityFactory extends ProductFactory {

    public static ProductEntity buildDefaultProductEntity(ArtistEntity artist) {
        return new ProductEntity(
                randomUUID(),
                "slug",
                "Name",
                "Description",
                "imageUrl",
                "category",
                Money.of(100.99, Currency.EUR),
                10,
                artist,
                new Date(),
                new Date());
    }

}
