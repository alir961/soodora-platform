package com.grooveconductor.infrastructure.event;

import com.grooveconductor.domain.common.UnknownSlugException;
import com.grooveconductor.domain.event.CreateEventCommand;
import com.grooveconductor.domain.event.EventDTO;
import com.grooveconductor.domain.event.EventRepository;
import com.grooveconductor.domain.user.UnknownIdentifierException;
import com.grooveconductor.infrastructure.artist.ArtistEntity;
import com.grooveconductor.infrastructure.artist.ArtistJpaRepository;
import com.grooveconductor.infrastructure.common.DomainEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.grooveconductor.infrastructure.common.SlugGenerator.generateSlug;

@Repository
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepository {

    private final EventJpaRepository eventRepository;
    private final ArtistJpaRepository artistRepository;

    @Override
    public EventDTO save(CreateEventCommand command, String artistSlug) {
        EventEntity event = new EventEntity();
        ArtistEntity artist = artistRepository.findBySlug(artistSlug)
                .orElseThrow(() -> new UnknownSlugException("Invalid artist slug: " + artistSlug));

        event.setSlug(generateUniqueSlug());
        event.setType(command.type());
        event.setTitle(command.title());
        event.setDescription(command.description());
        event.setUrl(command.url());
        event.setThumbnailUrl(command.thumbnailUrl());
        event.setAddress(command.address());
        event.setDateTime(command.dateTime());
        event = eventRepository.save(event);
        artist.getEvents().add(event);
        artistRepository.save(artist);
        return event.toDTO();
    }

    @Override
    public EventDTO find(String slug) {
        return findArtistBySlug(slug)
                .toDTO();
    }

    @Override
    public List<EventDTO> findAll() {
        return eventRepository.findAll().stream()
                .map(DomainEntity::toDTO)
                .toList();
    }

    @Override
    public void delete(String slug) {
        eventRepository.findBySlug(slug)
                .ifPresent(eventRepository::delete);
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (eventRepository.findBySlug(slug).isPresent())
            slug = generateSlug();
        return slug;
    }

    private EventEntity findArtistBySlug(String slug) {
        return eventRepository.findBySlug(slug)
                .orElseThrow(() -> new UnknownIdentifierException("Unable to find artist '" + slug + "'"));
    }

}
