package com.soodora.infrastructure.event;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.event.Event;
import com.soodora.domain.event.EventRepository;
import com.soodora.domain.user.User;
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
    public Event save(Event event) {
        EventEntity eventEntity = eventRepository.findBySlug(event.getSlug()).orElse(new EventEntity());
        String slug = event.getSlug() == null ? generateUniqueSlug() : event.getSlug();

        eventEntity.setSlug(slug);
        eventEntity.setType(event.getType());
        eventEntity.setTitle(event.getTitle());
        eventEntity.setDescription(event.getDescription());
        eventEntity.setUrl(event.getUrl());
        eventEntity.setThumbnailUrl(event.getThumbnailUrl());
        eventEntity.setLatitude(event.getLatitude());
        eventEntity.setLongitude(event.getLongitude());
        eventEntity.setDateTime(event.getDateTime());
        eventEntity = eventRepository.save(eventEntity);

        for (User artist : event.getArtists()) {
            ArtistEntity artistEntity = artistRepository.findByUserSlug(artist.getSlug())
                    .orElseThrow(
                            () -> new UnknownSlugException("Unable to find artist with slug: #" + artist.getSlug()));
            boolean hasEvent = artistEntity.getEvents().stream()
                    .anyMatch(entity -> entity.getSlug().equals(event.getSlug()));

            if (hasEvent)
                artistEntity.getEvents().removeIf(entity -> entity.getSlug().equals(event.getSlug()));
            artistEntity.getEvents().add(eventEntity);
            artistRepository.save(artistEntity);
        }
        return eventEntity.aggregate();
    }

    @Override
    @Transactional
    public Event find(String eventSlug) {
        return findEventBySlugOrThrows(eventSlug)
                .aggregate();
    }

    @Override
    @Transactional
    public List<Event> findAll() {
        return eventRepository.findAll().stream()
                .map(DomainEntityAdapter::aggregate)
                .toList();
    }

    @Override
    @Transactional
    public void delete(String eventSlug) {
        eventRepository.findBySlug(eventSlug)
                .ifPresent(eventRepository::delete);
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (eventRepository.findBySlug(slug).isPresent())
            slug = generateSlug();
        return slug;
    }

    private EventEntity findEventBySlugOrThrows(String slug) {
        return eventRepository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find event with slug: #" + slug));
    }

}
