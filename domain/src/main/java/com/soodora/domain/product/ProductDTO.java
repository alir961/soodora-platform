package com.soodora.domain.product;

import com.soodora.domain.artist.ArtistDTO;

import java.util.Date;

public record ProductDTO(
        String slug,
        String name,
        String description,
        String imageUrl,
        String category,
        long price,
        long quantity,
        ArtistDTO vendor,
        Date createdAt) {

}
