package com.grooveconductor.test.factory;

import com.grooveconductor.domain.product.ProductDTO;
import lombok.NoArgsConstructor;

import static com.grooveconductor.test.factory.UserFactory.buildDefaultUser;
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
                buildDefaultUser());
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
                buildDefaultUser());
    }

}
