package com.grooveconductor.domain.dto;

public record ProductDTO(String slug, String name, String description, String imageUrl, String category, long price,
                         long quantity, String userSlug) {

}
