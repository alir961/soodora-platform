package com.soodora.api.event;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.event.EventDTO;
import com.soodora.domain.user.User;
import com.soodora.service.artist.UnknownArtistException;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.event.CreateEventRequest;
import com.soodora.service.event.EventService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/artists/events")
public class EventController implements EventResource {

    private final EventService eventService;

    @Override
    @PostMapping
    public ResponseEntity<?> createEvent(
            @AuthenticationPrincipal User user,
            @RequestBody CreateEventRequest request) {
        try {
            EventDTO event = eventService.createEvent(request.toCommand(), user);

            return status(CREATED).body(event.slug());
        } catch (UnknownArtistException exception) {
            return status(FORBIDDEN).body(exception.getMessage());
        } catch (InvalidRequestException exception) {
            return badRequest().body(exception.getMessage());
        }
    }

    @Override
    @GetMapping
    public ResponseEntity<?> getEvents(
            @RequestParam String artistSlug) {
        try {
            List<EventDTO> events = eventService.getArtistEvents(artistSlug);

            if (events.isEmpty())
                return noContent().build();
            return ok().body(events);
        } catch (UnknownSlugException exception) {
            return noContent().build();
        }
    }

    @Override
    @GetMapping(":me")
    public ResponseEntity<?> getAuthenticatedArtistEvents(
            @AuthenticationPrincipal User user) {
        try {
            List<EventDTO> events = eventService.getArtistEvents(user);

            if (events.isEmpty())
                return noContent().build();
            return ok().body(events);
        } catch (UnknownArtistException exception) {
            return status(FORBIDDEN).body(exception.getMessage());
        }
    }

}
