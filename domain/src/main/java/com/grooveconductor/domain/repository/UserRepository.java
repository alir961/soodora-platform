package com.grooveconductor.domain.repository;

import com.grooveconductor.domain.command.RegisterCommand;
import com.grooveconductor.domain.dto.ArtistDTO;
import com.grooveconductor.domain.dto.Identifier;
import com.grooveconductor.domain.dto.UserDTO;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    UserDTO save(RegisterCommand command);
    UserDTO find(Identifier identifier);
    List<UserDTO> findAll();
    void delete(Identifier identifier);
    boolean isUsed(Identifier identifier);
    String retrieveEncodedPassword(Identifier identifier);
    Optional<ArtistDTO> retrieveArtist(Identifier identifier);

}
