package com.soodora.domain.linkhub;

import com.soodora.domain.artist.ArtistDTO;
import com.soodora.domain.link.LinkDTO;

import java.util.List;

public record LinkHubDTO(
        String slug,
        String title,
        String description,
        List<LinkDTO> links,
        ArtistDTO owner) {

}
