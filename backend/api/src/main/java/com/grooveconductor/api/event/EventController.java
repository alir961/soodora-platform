package com.grooveconductor.api.event;

import com.grooveconductor.domain.common.UnknownSlugException;
import com.grooveconductor.domain.event.EventDTO;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.service.artist.UnknownArtistException;
import com.grooveconductor.service.common.InvalidRequestException;
import com.grooveconductor.service.event.CreateEventRequest;
import com.grooveconductor.service.event.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @AuthenticationPrincipal UserDTO user,
            @RequestBody CreateEventRequest request
    ) {
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
            @RequestParam String artistSlug
    ) {
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
            @AuthenticationPrincipal UserDTO user
    ) {
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
