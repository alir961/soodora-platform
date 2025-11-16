package com.soodora.infrastructure.product;

import com.soodora.domain.common.Currency;
import com.soodora.infrastructure.common.MoneyEmbeddable;
import com.soodora.test.factory.ProductFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import static java.util.UUID.randomUUID;

import static com.soodora.domain.product.ProductStatus.ACTIVE;
import static com.soodora.infrastructure.artist.ArtistEntityFactory.buildDefaultArtistEntity;
import static com.soodora.infrastructure.sale.SaleEntityFactory.buildDefaultSaleEntity;

public class ProductEntityFactory extends ProductFactory {

    public static ProductEntity buildDefaultProductEntity() {
        return new ProductEntity(
                randomUUID(),
                "slug",
                "Name",
                "Description",
                "imageUrl",
                "category",
                new MoneyEmbeddable(BigDecimal.valueOf(100.99), Currency.EUR),
                10,
                buildDefaultArtistEntity(),
                ACTIVE,
                new Date(),
                new Date(),
                Set.of(buildDefaultSaleEntity()));
    }

}
