package com.soodora.infrastructure.user;

import com.soodora.domain.artist.ArtistDTO;
import com.soodora.domain.auth.value.Role;
import com.soodora.domain.playlist.PlaylistDTO;
import com.soodora.domain.post.CommentDTO;
import com.soodora.domain.post.LikeDTO;
import com.soodora.domain.user.*;
import com.soodora.domain.user.value.Identifier;
import com.soodora.infrastructure.artist.ArtistEntity;
import com.soodora.infrastructure.artist.ArtistJpaRepository;
import com.soodora.infrastructure.auth.RoleEntity;
import com.soodora.infrastructure.auth.RoleJpaRepository;
import com.soodora.infrastructure.common.DomainEntityAdapter;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

import static com.soodora.domain.common.SlugGenerator.generateSlug;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userRepository;
    private final ArtistJpaRepository artistRepository;
    private final RoleJpaRepository roleRepository;

    @Override
    @Transactional
    public User save(User user) {
        UserEntity userEntity = userRepository.findBySlug(user.getSlug()).orElse(new UserEntity());
        String slug = user.getSlug() == null ? generateUniqueSlug() : user.getSlug();
        Set<RoleEntity> roles = user.getRoles().stream()
                .map(Role::completeRole)
                .map(roleRepository::findByName)
                .collect(toSet());

        if (!userEntity.isArtist() && roles.stream().anyMatch(RoleEntity::isArtistRole)) {
            ArtistEntity artist = new ArtistEntity();

            artist.setUser(userEntity);
            artistRepository.save(artist);
        } else if (userEntity.isArtist() && roles.stream().noneMatch(RoleEntity::isArtistRole)) {
            artistRepository.delete(userEntity.getArtist());
            userEntity.setArtist(null);
        }

        userEntity.setSlug(slug);
        userEntity.setUsername(user.getUsername().value());
        userEntity.setEmail(user.getEmail().value());
        userEntity.setPassword(user.getPassword());
        userEntity.setFirstname(user.getFirstname());
        userEntity.setLastname(user.getLastname());
        userEntity.setDescription(user.getDescription());
        userEntity.setAvatarUrl(user.getAvatarUrl() == null ? null : user.getAvatarUrl().value());
        userEntity.setRoles(roles);

        return userRepository.save(userEntity).aggregate();
    }

    @Override
    @Transactional
    public boolean isUsed(Identifier identifier) {
        return findUserByIdentifier(identifier).isPresent();
    }

    @Override
    @Transactional
    public User find(Identifier identifier) throws UnknownIdentifierException {
        return findUserByIdentifierOrThrows(identifier).aggregate();
    }

    @Override
    @Transactional
    public User findWithFollowers(Identifier identifier) {
        return findUserByIdentifierOrThrows(identifier).aggregateFollowers();
    }

    @Override
    @Transactional
    public Set<User> findAll() {
        return userRepository.findAll().stream()
                .map(DomainEntityAdapter::aggregate)
                .collect(toSet());
    }

    @Override
    @Transactional
    public Page<User> searchUsersByUsername(String usernameQuery, boolean onlyArtists, Pageable pageable) {
        if (onlyArtists) {
            return artistRepository
                    .findByUserUsernameContainingIgnoreCase(usernameQuery, pageable)
                    .map(artist -> artist.getUser().aggregate());
        } else {
            return userRepository.findByUsernameContainingIgnoreCase(usernameQuery, pageable)
                    .map(DomainEntityAdapter::aggregate);
        }
    }

    @Override
    @Transactional
    public void delete(Identifier identifier) {
        UserEntity user = findUserByIdentifierOrThrows(identifier);

        userRepository.deleteById(user.getId());
    }

    @Override
    @Transactional
    public void deleteAvatar(Identifier identifier) {
        UserEntity user = findUserByIdentifierOrThrows(identifier);

        user.setAvatarUrl(null);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public Optional<ArtistDTO> retrieveArtist(Identifier identifier) {
        return ofNullable(findUserByIdentifierOrThrows(identifier).getArtist())
                .map(DomainEntityAdapter::aggregate);
    }

    @Override
    @Transactional
    public List<PlaylistDTO> retrievePlaylists(Identifier identifier) {
        return findUserByIdentifierOrThrows(identifier).getPlaylists().stream()
                .map(DomainEntityAdapter::aggregate)
                .toList();
    }

    @Override
    @Transactional
    public List<LikeDTO> retrieveLikes(Identifier identifier) {
        return findUserByIdentifierOrThrows(identifier)
                .getLikes()
                .stream()
                .map(DomainEntityAdapter::aggregate)
                .toList();
    }

    @Override
    @Transactional
    public List<CommentDTO> retrievePostComments(Identifier identifier) {
        return findUserByIdentifierOrThrows(identifier)
                .getComments()
                .stream()
                .map(DomainEntityAdapter::aggregate)
                .toList();
    }

    private Optional<UserEntity> findUserByIdentifier(Identifier identifier) {
        String value = identifier.value();

        return switch (identifier.type()) {
            case EMAIL -> userRepository.findByEmail(value);
            case SLUG -> userRepository.findBySlug(value);
            case USERNAME -> userRepository.findByUsername(value);
        };
    }

    private UserEntity findUserByIdentifierOrThrows(Identifier identifier) throws UnknownIdentifierException {
        Optional<UserEntity> findEntity = findUserByIdentifier(identifier);

        return findEntity
                .orElseThrow(() -> new UnknownIdentifierException("Unable to find user '" + identifier.value() + "'"));
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (userRepository.findBySlug(slug).isPresent())
            slug = generateSlug();
        return slug;
    }

}
