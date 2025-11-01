package com.soodora.service.event;

import com.soodora.domain.event.CreateEventCommand;
import com.soodora.domain.event.EventType;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

import java.time.LocalDateTime;
import java.util.Arrays;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateEventRequest(
        @Schema(description = "Type of the event", example = "DANCE_MUSIC", defaultValue = "DANCE_MUSIC") String type,
        @Schema(description = "Title of the event", example = "Groove Night", defaultValue = "New Event") String title,
        @Schema(description = "Description of the event", example = "A night of groovy tunes and good vibes.", defaultValue = "No description available") String description,
        @Schema(description = "URL to the event page", example = "https://example.com/event", defaultValue = "https://example.com") String url,
        @Schema(description = "URL to the event thumbnail image", example = "https://example.com/thumbnail.jpg", defaultValue = "https://example.com/default-thumbnail.jpg") String thumbnailUrl,
        @Schema(description = "Address where the event takes place", example = "123 Groove Street", defaultValue = "Unknown Location") String address,
        @Schema(description = "Date and time of the event", example = "2025-01-15T20:00:00", defaultValue = "2025-01-01T00:00:00") LocalDateTime dateTime)
        implements
            Request<CreateEventCommand> {

    @Override
    public CreateEventCommand toCommand() throws InvalidRequestException {
        if (title == null || title.isBlank())
            throw new InvalidRequestException("Title cannot be null or blank");

        try {
            return new CreateEventCommand(
                    EventType.valueOf(type.toUpperCase()),
                    title.trim(),
                    description.trim(),
                    url.trim(),
                    thumbnailUrl.trim(),
                    address.trim(),
                    dateTime);
        } catch (IllegalArgumentException exception) {
            throw new InvalidRequestException("Event type must be one of " + Arrays.toString(EventType.values()));
        }
    }

}
