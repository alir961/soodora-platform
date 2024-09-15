package com.grooveconductor.domain.repository;

import com.grooveconductor.domain.command.CreateArtistCommand;
import com.grooveconductor.domain.dto.ArtistDTO;
import com.grooveconductor.domain.dto.UserDTO;

import java.util.List;

public interface ArtistRepository {

    ArtistDTO save(CreateArtistCommand command, UserDTO user);
    ArtistDTO find(String slug);
    List<ArtistDTO> findAll();
    void delete(String slug);
    boolean isUsed(String slug);

}
