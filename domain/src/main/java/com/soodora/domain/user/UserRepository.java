package com.soodora.domain.user;

import com.soodora.domain.artist.ArtistDTO;
import com.soodora.domain.auth.RegisterCommand;
import com.soodora.domain.linkpage.LinkPageDTO;
import com.soodora.domain.playlist.PlaylistDTO;
import com.soodora.domain.post.CommentDTO;
import com.soodora.domain.post.LikeDTO;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository {

    UserDTO save(RegisterCommand command);
    UserDTO update(UpdateUserCommand command, Identifier identifier);
    UserDTO find(Identifier identifier);
    List<UserDTO> findAll();
    Page<UserDTO> searchUsersByUsername(String usernameQuery, boolean onlyArtists, Pageable pageable);
    void delete(Identifier identifier);
    void deleteAvatar(Identifier identifier);
    boolean isUsed(Identifier identifier);
    String retrieveEncodedPassword(Identifier identifier);
    Optional<ArtistDTO> retrieveArtist(Identifier identifier);
    List<PlaylistDTO> retrievePlaylists(Identifier identifier);
    List<LikeDTO> retrieveLikes(Identifier identifier);
    List<CommentDTO> retrievePostComments(Identifier identifier);
    LinkPageDTO retrieveLinkPage(Identifier identifier);
    void follow(Identifier follower, Identifier following);
    Set<UserDTO> retrieveFollowers(Identifier identifier);
    Set<UserDTO> retrieveFollowing(Identifier identifier);
    void unfollow(Identifier follower, Identifier following);

}
