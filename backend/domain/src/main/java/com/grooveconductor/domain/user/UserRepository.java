package com.grooveconductor.domain.user;

import com.grooveconductor.domain.artist.ArtistDTO;
import com.grooveconductor.domain.auth.RegisterCommand;
import com.grooveconductor.domain.linkpage.LinkPageDTO;
import com.grooveconductor.domain.playlist.PlaylistDTO;
import com.grooveconductor.domain.post.CommentDTO;
import com.grooveconductor.domain.post.LikeDTO;
import com.grooveconductor.domain.post.PostDTO;
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
    List<PostDTO> retrievePosts(Identifier identifier);
    List<LikeDTO> retrieveLikes(Identifier identifier);
    List<CommentDTO> retrievePostComments(Identifier identifier);
    LinkPageDTO retrieveLinkPage(Identifier identifier);

}
