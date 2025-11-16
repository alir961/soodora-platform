package com.soodora.domain.event;

import com.soodora.domain.artist.ArtistDTO;

import java.time.LocalDateTime;
import java.util.List;

public record EventDTO(
        String slug,
        EventType type,
        String title,
        String description,
        String url,
        String thumbnailUrl,
        String address,
        LocalDateTime dateTime,
        List<ArtistDTO> artists) {

}
