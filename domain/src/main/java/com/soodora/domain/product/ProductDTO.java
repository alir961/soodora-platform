package com.soodora.domain.product;

import com.soodora.domain.user.PublicUserDTO;

public record ProductDTO(
        String slug,
        String name,
        String description,
        String imageUrl,
        String category,
        long price,
        long quantity,
        PublicUserDTO vendor) {

}
