package com.grooveconductor.api.event;

import com.grooveconductor.domain.event.EventDTO;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.service.artist.UnknownArtistException;
import com.grooveconductor.service.common.InvalidRequestException;
import com.grooveconductor.service.event.CreateEventRequest;
import com.grooveconductor.service.event.EventService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Event Controller")
@RequestMapping(path = "/events")
@SecurityRequirement(name = "bearerAuth")
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<String> createEvent(
            @AuthenticationPrincipal UserDTO user,
            @RequestBody CreateEventRequest request
    ) {
        try {
            eventService.createEvent(request.toCommand(), user);
            return ok().build();
        } catch (UnknownArtistException exception) {
            return status(FORBIDDEN).body(exception.getMessage());
        } catch (InvalidRequestException exception) {
            return badRequest().body(exception.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<EventDTO>> getAuthenticatedArtistEvents(
            @AuthenticationPrincipal UserDTO user
    ) {
        try {
            List<EventDTO> events = eventService.getArtistEvents(user);

            return ok().body(events);
        } catch (UnknownArtistException exception) {
            return status(FORBIDDEN).build();
        }
    }

}
