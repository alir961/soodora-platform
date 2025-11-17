package com.soodora.api.linkhub;

import com.soodora.domain.linkhub.LinkHub;
import com.soodora.domain.user.PublicUserDTO;

import java.util.List;

public record LinkHubDTO(
        String slug,
        String title,
        String description,
        List<LinkDTO> links,
        PublicUserDTO owner) {

    public static LinkHubDTO build(LinkHub linkHub) {
        return new LinkHubDTO(
                linkHub.getSlug(),
                linkHub.getTitle(),
                linkHub.getDescription(),
                linkHub.getLinks().stream().map(LinkDTO::build).toList(),
                PublicUserDTO.build(linkHub.getOwner()));
    }

}
