package com.soodora.service.event;

import com.soodora.domain.artist.ArtistRepository;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.event.Event;
import com.soodora.domain.event.EventRepository;
import com.soodora.domain.event.SaveEventCommand;
import com.soodora.service.artist.UnknownArtistException;
import com.soodora.service.shared.InvalidPermissionException;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.time.LocalDateTime.now;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.soodora.domain.event.EventType.FESTIVAL;
import static com.soodora.test.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    private static final String ARTIST_SLUG = ARTIST_USER.getSlug();

    private static final SaveEventCommand VALID_COMMAND = new SaveEventCommand(FESTIVAL, "Title", "", "", "", 0, 0,
            now());

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private EventService eventService;

    @Test
    void givenValidArtistSlug_whenGetArtistEvents_thenReturnEvents() {
        // GIVEN
        when(artistRepository.retrieveEvents(ARTIST_SLUG))
                .thenReturn(singletonList(EVENT));

        // WHEN
        List<Event> events = eventService.getArtistEvents(ARTIST_SLUG);

        // THEN
        assertEquals(1, events.size());
        assertSame(EVENT, events.getFirst());
    }

    @Test
    void givenInvalidArtistSlug_whenGetArtistEvents_thenThrowUnknownArtistException() {
        // GIVEN
        when(artistRepository.retrieveEvents(ARTIST_SLUG))
                .thenThrow(UnknownSlugException.class);

        // WHEN
        Executable executable = () -> eventService.getArtistEvents(ARTIST_SLUG);

        // THEN
        assertThrows(UnknownArtistException.class, executable);
    }

    @Test
    void givenEventArtist_whenUpdateEvent_thenReturnUpdatedEvent() {
        // GIVEN
        when(eventRepository.find(EVENT.getSlug()))
                .thenReturn(EVENT);
        when(eventRepository.save(any()))
                .thenReturn(EVENT);

        // WHEN
        Event event = eventService.updateEvent(ARTIST_USER, EVENT.getSlug(), VALID_COMMAND);

        // THEN
        assertSame(EVENT, event);
    }

    @Test
    void givenAdministrator_whenUpdateEvent_thenReturnUpdatedEvent() {
        // GIVEN
        when(eventRepository.find(EVENT.getSlug()))
                .thenReturn(EVENT);
        when(eventRepository.save(any()))
                .thenReturn(EVENT);

        // WHEN
        Event event = eventService.updateEvent(ADMIN, EVENT.getSlug(), VALID_COMMAND);

        // THEN
        assertSame(EVENT, event);
    }

    @Test
    void givenInvalidArtist_whenUpdateEvent_thenThrowInvalidPermissionException() {
        // GIVEN
        when(eventRepository.find(EVENT.getSlug()))
                .thenReturn(EVENT);

        // WHEN
        Executable executable = () -> eventService.updateEvent(USER, EVENT.getSlug(), VALID_COMMAND);

        // THEN
        assertThrows(InvalidPermissionException.class, executable, "Only an artist of the event can update it");
    }

    @Test
    void givenEventArtist_whenDeleteEvent_thenEventIsDeleted() {
        // GIVEN
        when(eventRepository.find(EVENT.getSlug()))
                .thenReturn(EVENT);

        // WHEN
        eventService.deleteEvent(ARTIST_USER, EVENT.getSlug());

        // THEN
        verify(eventRepository)
                .delete(EVENT.getSlug());
    }

    @Test
    void givenAdministrator_whenDeleteEvent_thenReturnUpdatedEvent() {
        // GIVEN
        when(eventRepository.find(EVENT.getSlug()))
                .thenReturn(EVENT);

        // WHEN
        eventService.deleteEvent(ADMIN, EVENT.getSlug());

        // THEN
        verify(eventRepository)
                .delete(EVENT.getSlug());
    }

    @Test
    void givenInvalidArtist_whenDeleteEvent_thenThrowInvalidPermissionException() {
        // GIVEN
        when(eventRepository.find(EVENT.getSlug()))
                .thenReturn(EVENT);

        // WHEN
        Executable executable = () -> eventService.updateEvent(USER, EVENT.getSlug(), VALID_COMMAND);

        // THEN
        assertThrows(InvalidPermissionException.class, executable, "Only an artist of the event can update it");
    }

}
