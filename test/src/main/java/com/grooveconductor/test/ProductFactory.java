package com.grooveconductor.test;

import com.grooveconductor.domain.dto.ProductDTO;

public class ProductFactory {

    public static ProductDTO buildDefaultProduct() {
        return new ProductDTO("default-slug", "Default Product Name", "Default Description",
            "default-image-url", "default-category", 100, 10, "default-user-slug");
    }

}