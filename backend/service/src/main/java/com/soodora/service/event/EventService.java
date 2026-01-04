package com.soodora.service.event;

import com.soodora.domain.artist.ArtistRepository;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.event.Event;
import com.soodora.domain.event.EventRepository;
import com.soodora.domain.event.SaveEventCommand;
import com.soodora.domain.user.User;
import com.soodora.service.artist.UnknownArtistException;
import com.soodora.service.shared.InvalidPermissionException;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import static java.util.List.of;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ArtistRepository artistRepository;

    public Event createEvent(User user, SaveEventCommand command) {
        Event event = command.buildEvent(null, of(user));

        return eventRepository.save(event);
    }

    public List<Event> getArtistEvents(String artistSlug) {
        try {
            return artistRepository.retrieveEvents(artistSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownArtistException(exception.getMessage());
        }
    }

    public Event updateEvent(User user, String eventSlug, SaveEventCommand command) {
        try {
            Event event = eventRepository.find(eventSlug);

            if (!event.hasAccess(user))
                throw new InvalidPermissionException("Only an artist of the event can update it");
            return eventRepository.save(command.buildEvent(eventSlug, event.getArtists()));
        } catch (UnknownSlugException exception) {
            throw new UnknownEventException(exception.getMessage());
        }
    }

    public void deleteEvent(User user, String eventSlug) {
        try {
            Event event = eventRepository.find(eventSlug);

            if (!event.hasAccess(user))
                throw new InvalidPermissionException("Only an artist of the event can delete it");
            eventRepository.delete(eventSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownEventException(exception.getMessage());
        }
    }

}
