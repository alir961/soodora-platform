package com.soodora.domain.linkpage;

import com.soodora.domain.artist.ArtistDTO;
import com.soodora.domain.link.LinkDTO;

import java.util.List;

public record LinkPageDTO(
        String slug,
        String title,
        String description,
        List<LinkDTO> links,
        ArtistDTO owner) {

}
