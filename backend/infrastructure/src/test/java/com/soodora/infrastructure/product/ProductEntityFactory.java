package com.soodora.infrastructure.product;

import com.soodora.infrastructure.artist.ArtistEntity;
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
                100,
                10,
                artist,
                new Date(),
                new Date());
    }

}
