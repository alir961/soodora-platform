package com.grooveconductor.domain.artist;

public record ArtistDTO(
    String slug,
    String name,
    String description,
    String imageUrl
) {

}
