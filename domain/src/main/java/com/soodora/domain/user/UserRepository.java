package com.soodora.domain.user;

import com.soodora.domain.artist.ArtistDTO;
import com.soodora.domain.post.CommentDTO;
import com.soodora.domain.post.LikeDTO;
import com.soodora.domain.user.value.Identifier;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository {

    User save(User user);

    boolean isUsed(Identifier identifier);
    User find(Identifier identifier);
    User findWithFollowers(Identifier identifier);
    Set<User> findAll();
    Optional<User> findByUsername(String username);
    Page<User> searchUsersByUsername(String usernameQuery, boolean onlyArtists, Pageable pageable);

    void delete(Identifier identifier);
    void deleteAvatar(Identifier identifier);

    Optional<ArtistDTO> retrieveArtist(Identifier identifier);
    List<LikeDTO> retrieveLikes(Identifier identifier);
    List<CommentDTO> retrievePostComments(Identifier identifier);

}
