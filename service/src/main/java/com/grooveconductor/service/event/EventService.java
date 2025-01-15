package com.grooveconductor.service.event;

import com.grooveconductor.domain.artist.ArtistDTO;
import com.grooveconductor.domain.artist.ArtistRepository;
import com.grooveconductor.domain.event.CreateEventCommand;
import com.grooveconductor.domain.event.EventDTO;
import com.grooveconductor.domain.event.EventRepository;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.domain.user.UserRepository;
import com.grooveconductor.service.artist.UnknownArtistException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ArtistRepository artistRepository;
    private final UserRepository userRepository;

    public void createEvent(CreateEventCommand command, UserDTO user) {
        ArtistDTO artist = retrieveArtistOrThrows(user);

        eventRepository.save(command, artist.slug());
    }

    public List<EventDTO> getArtistEvents(UserDTO user) {
        ArtistDTO artist = retrieveArtistOrThrows(user);

        return artistRepository.retrieveEvents(artist.slug());
    }

    private ArtistDTO retrieveArtistOrThrows(UserDTO user) {
        return userRepository.retrieveArtist(user.identifier())
                .orElseThrow(() -> new UnknownArtistException("User is not an artist"));
    }

}
