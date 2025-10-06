package com.soodora.service.event;

import com.soodora.domain.artist.ArtistDTO;
import com.soodora.domain.artist.ArtistRepository;
import com.soodora.domain.event.CreateEventCommand;
import com.soodora.domain.event.EventDTO;
import com.soodora.domain.event.EventRepository;
import com.soodora.domain.user.UserDTO;
import com.soodora.domain.user.UserRepository;
import com.soodora.service.artist.UnknownArtistException;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ArtistRepository artistRepository;
    private final UserRepository userRepository;

    public EventDTO createEvent(CreateEventCommand command, UserDTO user) {
        ArtistDTO artist = retrieveArtistOrThrows(user);

        return eventRepository.save(command, artist.slug());
    }

    public List<EventDTO> getArtistEvents(UserDTO user) {
        ArtistDTO artist = retrieveArtistOrThrows(user);

        return artistRepository.retrieveEvents(artist.slug());
    }

    public List<EventDTO> getArtistEvents(String artistSlug) {
        return artistRepository.retrieveEvents(artistSlug);
    }

    private ArtistDTO retrieveArtistOrThrows(UserDTO user) {
        return userRepository.retrieveArtist(user.identifier())
                .orElseThrow(() -> new UnknownArtistException("User is not an artist"));
    }

}
