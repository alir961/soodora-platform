package com.soodora.service.user;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.post.PostDTO;
import com.soodora.domain.post.PostRepository;
import com.soodora.domain.product.ProductDTO;
import com.soodora.domain.product.ProductRepository;
import com.soodora.domain.user.*;
import com.soodora.service.auth.UserPrincipal;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.soodora.domain.user.IdentifierFactory.createFromEmail;
import static com.soodora.domain.user.IdentifierFactory.createFromSlug;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ProductRepository productRepository;

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

    public UserDTO updateUser(UpdateUserCommand command, String slug, MultipartFile imageFile) {
        try {
            return userRepository.update(command, createFromSlug(slug), imageFile);
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

    public Page<ProductDTO> retrieveProducts(Identifier identifier, Pageable pageable) {
        return productRepository.findUserProducts(identifier, pageable);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDTO user = retrieveUser(createFromEmail(username));

        log.info(user.toString());
        return new UserPrincipal(user);
    }

}
