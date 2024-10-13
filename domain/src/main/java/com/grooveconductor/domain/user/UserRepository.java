package com.grooveconductor.domain.user;

import com.grooveconductor.domain.auth.RegisterCommand;
import com.grooveconductor.domain.artist.ArtistDTO;

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
