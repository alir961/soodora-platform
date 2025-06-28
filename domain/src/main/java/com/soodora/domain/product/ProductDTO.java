package com.soodora.domain.product;

import com.soodora.domain.user.UserDTO;

public record ProductDTO(
        String slug,
        String name,
        String description,
        String imageUrl,
        String category,
        long price,
        long quantity,
        UserDTO vendor) {

}
