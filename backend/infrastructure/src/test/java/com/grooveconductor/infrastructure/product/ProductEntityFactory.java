package com.grooveconductor.infrastructure.product;

import com.grooveconductor.infrastructure.user.UserEntity;
import com.grooveconductor.test.factory.ProductFactory;

import java.util.Date;

import static java.util.UUID.randomUUID;

public class ProductEntityFactory extends ProductFactory {

    public static ProductEntity buildDefaultProductEntity(UserEntity user) {
        return new ProductEntity(
                randomUUID(),
                "slug",
                "Name",
                "Description",
                "imageUrl",
                "category",
                100,
                10,
                user,
                new Date(),
                new Date()
        );
    }

}
