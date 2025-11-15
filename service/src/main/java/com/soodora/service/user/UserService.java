package com.soodora.service.user;

import com.soodora.domain.auth.value.Role;
import com.soodora.domain.cdn.ImageStorageUploader;
import com.soodora.domain.post.PostDTO;
import com.soodora.domain.post.PostRepository;
import com.soodora.domain.product.ProductDTO;
import com.soodora.domain.product.ProductRepository;
import com.soodora.domain.user.*;
import com.soodora.domain.user.value.AvatarUrl;
import com.soodora.domain.user.value.Identifier;
import com.soodora.domain.user.value.Username;
import com.soodora.service.auth.UserPrincipal;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.util.stream.Collectors.toSet;

import static com.soodora.domain.user.value.Identifier.Factory.createFromUsername;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ProductRepository productRepository;
    private final ImageStorageUploader imageStorageUploader;

    public Set<User> retrieveAllUsers() {
        return userRepository.findAll();
    }

    public User retrieveUser(Identifier identifier) throws UnknownIdentifierException {
        try {
            return userRepository.find(identifier);
        } catch (UnknownIdentifierException exception) {
            throw new UnknownUserException(exception.getMessage());
        }
    }

    public Page<User> searchUsersByUsername(String usernameQuery, boolean onlyArtists, Pageable pageable) {
        return userRepository.searchUsersByUsername(usernameQuery, onlyArtists, pageable);
    }

    public User updateUser(Identifier userIdentifier, UserUpdateCommand updateCommand) {
        User user = retrieveUser(userIdentifier);
        Username username = new Username(updateCommand.username());
        AvatarUrl avatarUrl = null;

        if (updateCommand.avatar() != null) {
            String hostedAvatarUrl = imageStorageUploader.upload(updateCommand.avatar());

            avatarUrl = new AvatarUrl(hostedAvatarUrl);
        }
        user.updateUser(username, updateCommand.firstname(), updateCommand.lastname(),
                updateCommand.description(), avatarUrl);
        return userRepository.save(user);
    }

    public void updateUserRoles(Identifier identifier, Set<String> roles) {
        User user = retrieveUser(identifier);

        user.updateRoles(roles.stream()
                .map(Role::new)
                .collect(toSet()));
        userRepository.save(user);
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

    public void follow(Identifier followerId, Identifier followingId) throws UnknownUserException {
        try {
            User follower = userRepository.findWithFollowers(followerId);
            User following = userRepository.findWithFollowers(followingId);

            follower.follow(following);

            userRepository.save(follower);
            userRepository.save(following);
        } catch (UnknownIdentifierException exception) {
            throw new UnknownUserException(exception.getMessage());
        }
    }

    public void unfollow(Identifier followerId, Identifier followingId) throws UnknownUserException {
        try {
            User follower = userRepository.findWithFollowers(followerId);
            User following = userRepository.findWithFollowers(followingId);

            follower.unfollow(following);

            userRepository.save(follower);
            userRepository.save(following);
        } catch (UnknownIdentifierException exception) {
            throw new UnknownUserException(exception.getMessage());
        }
    }

    public Set<User> retrieveFollowers(Identifier identifier) throws UnknownUserException {
        try {
            User user = userRepository.findWithFollowers(identifier);

            return user.getFollowers();
        } catch (UnknownIdentifierException exception) {
            throw new UnknownUserException(exception.getMessage());
        }
    }

    public Set<User> retrieveFollowing(Identifier identifier) throws UnknownUserException {
        try {
            User user = userRepository.findWithFollowers(identifier);

            return user.getFollowing();
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
        User user = retrieveUser(createFromUsername(username));

        return new UserPrincipal(user);
    }

}
