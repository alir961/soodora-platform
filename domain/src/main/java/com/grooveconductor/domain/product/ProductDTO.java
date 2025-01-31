package com.grooveconductor.domain.product;

import com.grooveconductor.domain.user.UserDTO;

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
