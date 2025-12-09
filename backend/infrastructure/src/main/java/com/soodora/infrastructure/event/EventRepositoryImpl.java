package com.soodora.infrastructure.event;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.event.CreateEventCommand;
import com.soodora.domain.event.EventDTO;
import com.soodora.domain.event.EventRepository;
import com.soodora.domain.user.UnknownIdentifierException;
import com.soodora.infrastructure.artist.ArtistEntity;
import com.soodora.infrastructure.artist.ArtistJpaRepository;
import com.soodora.infrastructure.common.DomainEntityAdapter;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import static com.soodora.domain.common.SlugGenerator.generateSlug;

@Repository
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepository {

    private final EventJpaRepository eventRepository;
    private final ArtistJpaRepository artistRepository;

    @Override
    @Transactional
    public EventDTO save(CreateEventCommand command, String artistSlug) {
        EventEntity event = new EventEntity();
        ArtistEntity artist = artistRepository.findByUserSlug(artistSlug)
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
        return event.aggregate();
    }

    @Override
    @Transactional
    public EventDTO find(String slug) {
        return findArtistBySlug(slug)
                .aggregate();
    }

    @Override
    @Transactional
    public List<EventDTO> findAll() {
        return eventRepository.findAll().stream()
                .map(DomainEntityAdapter::aggregate)
                .toList();
    }

    @Override
    @Transactional
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
