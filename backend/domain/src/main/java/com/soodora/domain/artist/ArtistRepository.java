package com.soodora.domain.artist;

import com.soodora.domain.event.Event;
import com.soodora.domain.linkhub.LinkHub;
import com.soodora.domain.user.User;

import java.util.List;

public interface ArtistRepository {

    User find(String slug);

    List<User> findAll();

    void delete(String slug);

    List<Event> retrieveEvents(String slug);

    LinkHub retrieveLinkHub(String artistSlug);

}
