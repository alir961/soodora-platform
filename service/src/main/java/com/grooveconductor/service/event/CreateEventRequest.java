package com.grooveconductor.service.event;

import com.grooveconductor.domain.event.CreateEventCommand;
import com.grooveconductor.domain.event.EventType;
import com.grooveconductor.service.common.InvalidRequestException;
import com.grooveconductor.service.common.Request;

import java.time.LocalDateTime;
import java.util.Arrays;

public record CreateEventRequest(
        String type,
        String title,
        String description,
        String url,
        String thumbnailUrl,
        String address,
        LocalDateTime dateTime
) implements Request<CreateEventCommand> {

    @Override
    public CreateEventCommand toCommand() throws InvalidRequestException {
        if (title == null || title.isEmpty()) throw new InvalidRequestException("Title cannot be null or empty");

        try {
            return new CreateEventCommand(
                    EventType.valueOf(type.toUpperCase()),
                    title.trim(),
                    description.trim(),
                    url.trim(),
                    thumbnailUrl.trim(),
                    address.trim(),
                    dateTime
            );
        } catch (IllegalArgumentException exception) {
            throw new InvalidRequestException("Event type must be one of " + Arrays.toString(EventType.values()));
        }
    }

}
