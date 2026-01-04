package com.soodora.api.event;

import com.soodora.domain.event.EventType;
import com.soodora.domain.event.SaveEventCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import static java.util.stream.Collectors.joining;

public record SaveEventRequest(
        @Schema(description = "Category of the event. Must match one of the predefined types.", example = "DANCE_MUSIC", allowableValues = {
                "CONCERT", "FESTIVAL", "COMPETITION", "PERFORMANCE_SHOW", "EDUCATIONAL", "DANCE_MUSIC",
                "RELIGIOUS_SPIRITUAL", "OTHER"}) @NotBlank(message = "Type is mandatory") String type,

        @Schema(description = "Public title of the event.", example = "Summer Jazz Festival 2025", minLength = 3, maxLength = 100) @NotBlank(message = "Title is mandatory") @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters") String title,

        @Schema(description = "Detailed description of the event, including lineup or special notes.", example = "Join us for a night of smooth jazz and funk under the stars. Featuring local artists and special guests.", maxLength = 2000) @Size(max = 1000, message = "Description cannot exceed 1000 characters") String description,

        @Schema(description = "External URL for tickets or more information.", example = "https://www.ticketmaster.com/jazz-fest-2025") String url,

        @Schema(description = "Direct URL to the event poster or thumbnail image.", example = "https://res.cloudinary.com/demo/image/upload/jazz-poster.jpg") String thumbnailUrl,
        @Schema(description = "Geographic latitude of the venue.", example = "48.8566", minimum = "-90", maximum = "90") double latitude,
        @Schema(description = "Geographic longitude of the venue.", example = "2.3522", minimum = "-180", maximum = "180") double longitude,
        @Schema(description = "Date and time when the event starts (ISO 8601 format).", example = "2025-07-15T20:00:00", type = "string", format = "date-time") @NotNull(message = "Date and time are mandatory") LocalDateTime dateTime)
        implements
            Request<SaveEventCommand>{

    @Override
    public SaveEventCommand toCommand() throws InvalidRequestException {
        if (title == null || title.isBlank())
            throw new InvalidRequestException("Title cannot be null or blank");

        try {
            return new SaveEventCommand(
                    EventType.valueOf(type.toUpperCase()),
                    title.trim(),
                    description.trim(),
                    url.trim(),
                    thumbnailUrl.trim(),
                    latitude,
                    longitude,
                    dateTime);
        } catch (IllegalArgumentException exception) {
            String eventTypes = Stream.of(EventType.values())
                    .map(Enum::toString)
                    .collect(joining(", "));

            throw new InvalidRequestException("Event type must be one of " + eventTypes);
        }
    }

}
