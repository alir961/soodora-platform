package com.soodora.domain.artist;

import com.soodora.domain.event.EventDTO;
import com.soodora.domain.linkhub.LinkHub;
import com.soodora.domain.user.User;

import java.util.List;

public interface ArtistRepository {

    User find(String slug);

    List<User> findAll();

    void delete(String slug);

    List<EventDTO> retrieveEvents(String slug);

    LinkHub retrieveLinkHub(String slug);

}
