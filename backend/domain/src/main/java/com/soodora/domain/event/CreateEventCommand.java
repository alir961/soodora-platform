package com.soodora.domain.event;

import java.time.LocalDateTime;

public record CreateEventCommand(
        EventType type,
        String title,
        String description,
        String url,
        String thumbnailUrl,
        String address,
        LocalDateTime dateTime) {

}
