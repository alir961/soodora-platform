package com.grooveconductor.domain.user;

import com.grooveconductor.domain.artist.ArtistDTO;
import com.grooveconductor.domain.auth.RegisterCommand;
import com.grooveconductor.domain.playlist.PlaylistDTO;
import com.grooveconductor.domain.product.ProductDTO;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    UserDTO save(RegisterCommand command);
    UserDTO update(UpdateUserCommand command, Identifier identifier);
    UserDTO find(Identifier identifier);
    List<UserDTO> findAll();
    void delete(Identifier identifier);
    boolean isUsed(Identifier identifier);
    String retrieveEncodedPassword(Identifier identifier);
    Optional<ArtistDTO> retrieveArtist(Identifier identifier);
    List<ProductDTO> retrieveProducts(Identifier identifier);
    List<PlaylistDTO> retrievePlaylists(Identifier identifier);

}
