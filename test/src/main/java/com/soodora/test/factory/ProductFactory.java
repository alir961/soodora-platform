package com.soodora.test.factory;

import com.soodora.domain.common.Currency;
import com.soodora.domain.common.Money;
import com.soodora.domain.product.ProductDTO;

import java.math.BigDecimal;
import java.util.Date;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

import static com.soodora.test.factory.ArtistFactory.buildArtistDTO;

@NoArgsConstructor(access = PROTECTED)
public class ProductFactory {

    public static ProductDTO buildDefaultProduct() {
        return new ProductDTO(
                "slug",
                "Name",
                "Description",
                "imageUrl",
                "category",
                Money.of(BigDecimal.valueOf(100.99), Currency.EUR),
                10,
                buildArtistDTO("id"),
                new Date());
    }

    public static ProductDTO buildProduct(String suffix) {
        return new ProductDTO(
                "slug-" + suffix,
                "Name" + suffix,
                "Description",
                "imageUrl",
                "category",
                Money.of(BigDecimal.valueOf(100.99), Currency.EUR),
                10,
                buildArtistDTO("id" + suffix),
                new Date());
    }

}
