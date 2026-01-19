package com.soodora.api.linkhub;

import com.soodora.domain.link.Link;

public record LinkDTO(
        String slug,
        String title,
        String description,
        String url,
        String iconUrl) {

    public static LinkDTO build(Link link) {
        return new LinkDTO(
                link.getSlug(),
                link.getTitle(),
                link.getDescription(),
                link.getUrl() == null ? null : link.getUrl().value(),
                link.getIconUrl());
    }

}
