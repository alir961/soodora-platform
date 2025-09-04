package com.soodora.test.factory;

import com.soodora.domain.product.ProductDTO;

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
                100,
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
                100,
                10,
                buildArtistDTO("id" + suffix),
                new Date());
    }

}
