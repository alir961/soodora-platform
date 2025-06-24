package com.grooveconductor.service.user;

import com.grooveconductor.domain.common.UnknownSlugException;
import com.grooveconductor.domain.post.PostDTO;
import com.grooveconductor.domain.post.PostRepository;
import com.grooveconductor.domain.user.*;
import com.grooveconductor.service.auth.UserPrincipal;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.grooveconductor.domain.user.IdentifierFactory.createFromEmail;
import static com.grooveconductor.domain.user.IdentifierFactory.createFromSlug;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public List<UserDTO> retrieveAllUsers() {
        return userRepository.findAll();
    }

    public UserDTO retrieveUser(Identifier identifier) throws UnknownIdentifierException {
        try {
            return userRepository.find(identifier);
        } catch (UnknownIdentifierException exception) {
            throw new UnknownUserException(exception.getMessage());
        }
    }

    public Page<UserDTO> searchUsersByUsername(String usernameQuery, boolean onlyArtists, Pageable pageable) {
        return userRepository.searchUsersByUsername(usernameQuery, onlyArtists, pageable);
    }

    public boolean existsUser(Identifier identifier) {
        return userRepository.isUsed(identifier);
    }

    public UserDTO updateUser(UpdateUserCommand command, String slug) {
        try {
            return userRepository.update(command, createFromSlug(slug));
        } catch (UnknownSlugException exception) {
            throw new UserUnityException(exception.getMessage());
        }
    }

    public void deleteUser(Identifier identifier) throws UnknownUserException {
        try {
            userRepository.delete(identifier);
        } catch (UnknownIdentifierException exception) {
            throw new UnknownUserException(exception.getMessage());
        }
    }

    public void deleteUserAvatar(Identifier identifier) throws UnknownUserException {
        try {
            userRepository.deleteAvatar(identifier);
        } catch (UnknownIdentifierException exception) {
            throw new UnknownUserException(exception.getMessage());
        }
    }

    public void follow(Identifier follower, Identifier following) throws UnknownUserException {
        try {
            userRepository.follow(follower, following);
        } catch (UnknownIdentifierException exception) {
            throw new UnknownUserException(exception.getMessage());
        }
    }

    public Set<UserDTO> retrieveFollowers(Identifier identifier) throws UnknownUserException {
        try {
            return userRepository.retrieveFollowers(identifier);
        } catch (UnknownIdentifierException exception) {
            throw new UnknownUserException(exception.getMessage());
        }
    }

    public Set<UserDTO> retrieveFollowing(Identifier identifier) throws UnknownUserException {
        try {
            return userRepository.retrieveFollowing(identifier);
        } catch (UnknownIdentifierException exception) {
            throw new UnknownUserException(exception.getMessage());
        }
    }

    public void unfollow(Identifier follower, Identifier following) throws UnknownUserException {
        try {
            userRepository.unfollow(follower, following);
        } catch (UnknownIdentifierException exception) {
            throw new UnknownUserException(exception.getMessage());
        }
    }

    public Page<PostDTO> retrievePosts(Identifier identifier, Pageable pageable) {
        return postRepository.findUserPosts(identifier, pageable);
    }

    public Page<PostDTO> retrieveReposts(Identifier identifier, Pageable pageable) {
        return postRepository.findUserReposts(identifier, pageable);
    }

    public Page<PostDTO> retrieveAudios(Identifier identifier, Pageable pageable) {
        return postRepository.findUserAudios(identifier, pageable);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDTO user = retrieveUser(createFromEmail(username));

        log.info(user.toString());
        return new UserPrincipal(user);
    }

}
