package com.soodora.domain.artist;

import com.soodora.domain.event.EventDTO;
import com.soodora.domain.linkhub.LinkHub;

import java.util.List;

public interface ArtistRepository {

    ArtistDTO find(String slug);

    List<ArtistDTO> findAll();

    void delete(String slug);

    List<EventDTO> retrieveEvents(String slug);

    LinkHub retrieveLinkHub(String slug);

}
