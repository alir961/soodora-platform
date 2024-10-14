package com.grooveconductor.domain.artist;

import com.grooveconductor.domain.user.UserDTO;

import java.util.List;

public interface ArtistRepository {

    ArtistDTO save(CreateArtistCommand command, UserDTO user);
    ArtistDTO find(String slug);
    List<ArtistDTO> findAll();
    void delete(String slug);

}
