package com.grooveconductor.infrastructure.user;

import com.grooveconductor.domain.artist.ArtistDTO;
import com.grooveconductor.domain.auth.RegisterCommand;
import com.grooveconductor.domain.linkpage.LinkPageDTO;
import com.grooveconductor.domain.playlist.PlaylistDTO;
import com.grooveconductor.domain.post.CommentDTO;
import com.grooveconductor.domain.post.LikeDTO;
import com.grooveconductor.domain.post.PostDTO;
import com.grooveconductor.domain.product.ProductDTO;
import com.grooveconductor.domain.user.*;
import com.grooveconductor.infrastructure.common.DomainEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import static java.util.Optional.ofNullable;

import static com.grooveconductor.infrastructure.common.SlugGenerator.generateSlug;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userRepository;

    @Override
    public UserDTO save(RegisterCommand command) {
        UserEntity user = new UserEntity();

        user.setSlug(generateUniqueSlug());
        user.setUsername(command.username());
        user.setEmail(command.email());
        user.setPassword(command.password());
        user.setFirstname(command.firstname());
        user.setLastname(command.lastname());
        user.setRoles(new HashSet<>());
        return userRepository.save(user).toDTO();
    }

    @Override
    public UserDTO update(UpdateUserCommand command, Identifier identifier) {
        UserEntity existingUser = findUserByIdentifierOrThrows(identifier);

        if (command.getUsername() != null && !command.getUsername().isBlank()
                && !command.getUsername().equals(existingUser.getUsername()))
            existingUser.setUsername(command.getUsername());
        if (command.getFirstname() != null && !command.getFirstname().isBlank()
                && !command.getFirstname().equals(existingUser.getFirstname()))
            existingUser.setFirstname(command.getFirstname());
        if (command.getLastname() != null && !command.getLastname().isBlank()
                && !command.getLastname().equals(existingUser.getLastname()))
            existingUser.setLastname(command.getLastname());

        return userRepository.save(existingUser).toDTO();
    }

    @Override
    public UserDTO find(Identifier identifier) {
        return findUserByIdentifierOrThrows(identifier).toDTO();
    }

    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(DomainEntity::toDTO)
                .toList();
    }

    @Override
    public void delete(Identifier identifier) {
        UserEntity user = findUserByIdentifierOrThrows(identifier);

        userRepository.deleteById(user.getId());
    }

    @Override
    public boolean isUsed(Identifier identifier) {
        try {
            findUserByIdentifierOrThrows(identifier);
            return true;
        } catch (UnknownIdentifierException exception) {
            return false;
        }
    }

    @Override
    public String retrieveEncodedPassword(Identifier identifier) {
        return findUserByIdentifierOrThrows(identifier)
                .getPassword();
    }

    @Override
    public Optional<ArtistDTO> retrieveArtist(Identifier identifier) {
        return ofNullable(findUserByIdentifierOrThrows(identifier).getArtist())
                .map(DomainEntity::toDTO);
    }

    @Override
    public List<ProductDTO> retrieveProducts(Identifier identifier) {
        return findUserByIdentifierOrThrows(identifier).getProducts().stream()
                .map(DomainEntity::toDTO)
                .toList();
    }

    @Override
    public List<PlaylistDTO> retrievePlaylists(Identifier identifier) {
        return findUserByIdentifierOrThrows(identifier).getPlaylists().stream()
                .map(DomainEntity::toDTO)
                .toList();
    }

    @Override
    public List<PostDTO> retrievePosts(Identifier identifier) {
        return findUserByIdentifierOrThrows(identifier).getPosts().stream()
                .map(DomainEntity::toDTO)
                .toList();
    }

    @Override
    public LinkPageDTO retrieveLinkPage(Identifier identifier) throws LinkPageNotFoundException {
        UserEntity user = findUserByIdentifierOrThrows(identifier);
        if (user.getLinkPage() == null)
            throw new LinkPageNotFoundException("Link Page not found for user " + identifier);

        return user.getLinkPage().toDTO();
    }

    @Override
    public List<LikeDTO> retrieveLikes(Identifier identifier) {
        return findUserByIdentifierOrThrows(identifier)
                .getLikes()
                .stream()
                .map(DomainEntity::toDTO)
                .toList();
    }

    @Override
    public List<CommentDTO> retrievePostComments(Identifier identifier) {
        return findUserByIdentifierOrThrows(identifier)
                .getComments()
                .stream()
                .map(DomainEntity::toDTO)
                .toList();
    }

    private UserEntity findUserByIdentifierOrThrows(Identifier identifier) throws UnknownIdentifierException {
        String value = identifier.getValue();
        Optional<UserEntity> findEntity = switch (identifier.getType()) {
            case EMAIL -> userRepository.findByEmail(value);
            case SLUG -> userRepository.findBySlug(value);
            case USERNAME -> userRepository.findByUsername(value);
        };

        return findEntity.orElseThrow(() -> new UnknownIdentifierException("Unable to find user '" + value + "'"));
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (userRepository.findBySlug(slug).isPresent())
            slug = generateSlug();
        return slug;
    }

}
