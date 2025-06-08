package com.grooveconductor.domain.link;

public record LinkDTO(
        String slug,
        String title,
        String description,
        String url,
        String iconUrl) {

}
