package com.soodora.domain.event;

import java.util.List;

public interface EventRepository {

    Event save(Event event);

    Event find(String eventSlug);

    List<Event> findAll();

    void delete(String eventSlug);

}
