package com.grooveconductor.domain.user;

import com.grooveconductor.domain.artist.ArtistDTO;
import com.grooveconductor.domain.auth.RegisterCommand;
import com.grooveconductor.domain.linkpage.LinkPageDTO;
import com.grooveconductor.domain.playlist.PlaylistDTO;
import com.grooveconductor.domain.post.CommentDTO;
import com.grooveconductor.domain.post.LikeDTO;
import com.grooveconductor.domain.post.PostDTO;

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
    List<PostDTO> retrievePosts(Identifier identifier);
    List<LikeDTO> retrieveLikes(Identifier identifier);
    List<CommentDTO> retrievePostComments(Identifier identifier);
    LinkPageDTO retrieveLinkPage(Identifier identifier);
    void follow(Identifier follower, Identifier following);
    Set<UserDTO> retrieveFollowers(Identifier identifier);
    Set<UserDTO> retrieveFollowing(Identifier identifier);
    void unfollow(Identifier follower, Identifier following);

}
