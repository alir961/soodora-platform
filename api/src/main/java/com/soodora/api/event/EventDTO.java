package com.soodora.api.event;

import com.soodora.api.common.JsonResponse;
import com.soodora.domain.event.Event;
import com.soodora.domain.event.EventType;
import com.soodora.domain.user.PublicUserDTO;

import java.time.LocalDateTime;
import java.util.List;

public record EventDTO(
        String slug,
        EventType type,
        String title,
        String description,
        String url,
        String thumbnailUrl,
        double latitude,
        double longitude,
        LocalDateTime dateTime,
        List<PublicUserDTO> artists)
        implements
            JsonResponse {

    public static EventDTO build(Event event) {
        return new EventDTO(
                event.getSlug(),
                event.getType(),
                event.getTitle(),
                event.getDescription(),
                event.getUrl(),
                event.getThumbnailUrl(),
                event.getLatitude(),
                event.getLongitude(),
                event.getDateTime(),
                event.getArtists().stream()
                        .map(PublicUserDTO::build)
                        .toList());
    }

}
