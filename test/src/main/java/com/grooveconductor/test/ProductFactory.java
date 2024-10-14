package com.grooveconductor.test;

import com.grooveconductor.domain.product.ProductDTO;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

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
                "user-slug"
        );
    }

}