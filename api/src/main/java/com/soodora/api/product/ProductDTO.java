package com.soodora.api.product;

import com.soodora.api.common.JsonResponse;
import com.soodora.domain.common.Money;
import com.soodora.domain.product.Product;
import com.soodora.domain.user.PublicUserDTO;

import java.util.Date;

public record ProductDTO(
        String slug,
        String name,
        String description,
        String imageUrl,
        String category,
        Money price,
        long quantity,
        PublicUserDTO vendor,
        Date createdAt) implements JsonResponse {

    public static ProductDTO build(Product product) {
        PublicUserDTO vendor = PublicUserDTO.build(product.getVendor());

        return new ProductDTO(
                product.getSlug(),
                product.getName(),
                product.getDescription(),
                product.getImageUrl().value(),
                product.getCategory(),
                product.getPrice(),
                product.getQuantity(),
                vendor,
                product.getCreatedAt());
    }

}
