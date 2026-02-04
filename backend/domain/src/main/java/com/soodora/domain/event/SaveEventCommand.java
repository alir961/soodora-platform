package com.soodora.domain.event;

import com.soodora.domain.user.User;

import java.time.LocalDateTime;
import java.util.List;

public record SaveEventCommand(
        EventType type,
        String title,
        String description,
        String url,
        String thumbnailUrl,
        double latitude,
        double longitude,
        LocalDateTime dateTime) {

    public Event buildEvent(String eventSlug, List<User> artists) {
        return new Event(
                eventSlug,
                type,
                title,
                description,
                url,
                thumbnailUrl,
                latitude,
                longitude,
                dateTime,
                artists);
    }

}
