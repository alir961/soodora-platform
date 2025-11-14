package com.soodora.domain.artist;

import com.soodora.domain.event.EventDTO;
import com.soodora.domain.linkhub.LinkHubDTO;

import java.util.List;

public interface ArtistRepository {

    ArtistDTO find(String slug);

    List<ArtistDTO> findAll();

    void delete(String slug);

    List<EventDTO> retrieveEvents(String slug);

    LinkHubDTO retrieveLinkHub(String slug);

}
