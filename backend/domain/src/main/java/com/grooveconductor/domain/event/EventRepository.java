package com.grooveconductor.domain.event;

import java.util.List;

public interface EventRepository {

    EventDTO save(CreateEventCommand command, String artistSlug);

    EventDTO find(String slug);

    List<EventDTO> findAll();

    void delete(String slug);

}
