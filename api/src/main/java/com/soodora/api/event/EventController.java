package com.soodora.api.event;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.domain.event.Event;
import com.soodora.domain.user.User;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.event.EventService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController implements EventResource {

    private final EventService eventService;

    @Override
    @PostMapping
    @ResponseStatus(code = CREATED)
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<JsonResponse> createEvent(
            @AuthenticationPrincipal User user,
            @RequestBody SaveEventRequest request) {
        try {
            Event event = eventService.createEvent(user, request.toCommand());

            return new ResponseEntity<>(EventDTO.build(event), CREATED);
        } catch (InvalidRequestException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @Override
    @GetMapping
    public List<EventDTO> searchEvents(@RequestParam String artistSlug) {
        return eventService.getArtistEvents(artistSlug).stream()
                .map(EventDTO::build)
                .toList();
    }

    @PutMapping("/{eventSlug}")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<JsonResponse> updateEvent(@AuthenticationPrincipal User user, @PathVariable String eventSlug,
            @RequestBody SaveEventRequest request) {
        try {
            Event event = eventService.updateEvent(user, eventSlug, request.toCommand());

            return new ResponseEntity<>(EventDTO.build(event), OK);
        } catch (InvalidRequestException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @DeleteMapping("/{eventSlug}")
    @PreAuthorize("hasRole('ARTIST')")
    public void deleteEvent(@AuthenticationPrincipal User user, @PathVariable String eventSlug) {
        eventService.deleteEvent(user, eventSlug);
    }

}
