package com.soodora.infrastructure.user;

import com.soodora.domain.artist.ArtistDTO;
import com.soodora.domain.auth.RegisterCommand;
import com.soodora.domain.auth.RoleDTO;
import com.soodora.domain.cdn.FileData;
import com.soodora.domain.cdn.ImageStorageUploader;
import com.soodora.domain.post.CommentDTO;
import com.soodora.domain.post.LikeDTO;
import com.soodora.domain.user.*;
import com.soodora.infrastructure.artist.ArtistEntity;
import com.soodora.infrastructure.artist.ArtistJpaRepository;
import com.soodora.infrastructure.auth.RoleEntity;
import com.soodora.infrastructure.auth.RoleJpaRepository;
import com.soodora.infrastructure.common.DomainEntity;

import java.util.HashSet;
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
import static com.soodora.domain.common.StringValidator.isNotNullOrBlank;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userRepository;
    private final ArtistJpaRepository artistRepository;
    private final RoleJpaRepository roleRepository;
    private final ImageStorageUploader imageStorageUploader;

    @Override
    @Transactional
    public UserDTO save(RegisterCommand command) {
        UserEntity user = new UserEntity();

        user.setSlug(generateUniqueSlug());
        user.setUsername(command.username());
        user.setEmail(command.email());
        user.setPassword(command.password());
        user.setFirstname(command.firstname());
        user.setLastname(command.lastname());
        user.setDescription("");
        user.setRoles(new HashSet<>());
        return userRepository.save(user).toDTO();
    }

    @Override
    @Transactional
    public UserDTO update(UpdateUserCommand command, Identifier identifier, FileData fileData) {
        UserEntity existingUser = findUserByIdentifierOrThrows(identifier);

        if (isNotNullOrBlank(command.username()))
            existingUser.setUsername(command.username());
        if (isNotNullOrBlank(command.firstname()))
            existingUser.setFirstname(command.firstname());
        if (isNotNullOrBlank(command.lastname()))
            existingUser.setLastname(command.lastname());
        if (isNotNullOrBlank(command.description()))
            existingUser.setDescription(command.description());
        if (fileData != null && fileData.content() != null) {
            String imageUrl = imageStorageUploader.upload(fileData);
            existingUser.setAvatarUrl(imageUrl);
        }
        return userRepository.save(existingUser).toDTO();
    }

    @Override
    @Transactional
    public void updateRole(Identifier identifier, Set<RoleDTO> roles) {
        UserEntity user = findUserByIdentifierOrThrows(identifier);
        Set<RoleEntity> roleEntities = roles.stream()
                .map(role -> roleRepository.findByName(role.name()))
                .collect(toSet());

        if (!user.isArtist() && roles.stream().anyMatch(RoleDTO::isArtistRole)) {
            ArtistEntity artist = new ArtistEntity();

            artist.setUser(user);
            artistRepository.save(artist);
        } else if (user.isArtist() && roles.stream().noneMatch(RoleDTO::isArtistRole)) {
            artistRepository.delete(user.getArtist());
            user.setArtist(null);
        }

        user.setRoles(roleEntities);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserDTO find(Identifier identifier) {
        return findUserByIdentifierOrThrows(identifier).toDTO();
    }

    @Override
    @Transactional
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(DomainEntity::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public Page<UserDTO> searchUsersByUsername(String usernameQuery, boolean onlyArtists, Pageable pageable) {
        if (onlyArtists) {
            return artistRepository
                    .findByUserUsernameContainingIgnoreCase(usernameQuery, pageable)
                    .map(artist -> artist.getUser().toDTO());
        } else {
            return userRepository.findByUsernameContainingIgnoreCase(usernameQuery, pageable)
                    .map(UserEntity::toDTO);
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
    public boolean isUsed(Identifier identifier) {
        try {
            findUserByIdentifierOrThrows(identifier);
            return true;
        } catch (UnknownIdentifierException exception) {
            return false;
        }
    }

    @Override
    @Transactional
    public String retrieveEncodedPassword(Identifier identifier) {
        return findUserByIdentifierOrThrows(identifier)
                .getPassword();
    }

    @Override
    @Transactional
    public Optional<ArtistDTO> retrieveArtist(Identifier identifier) {
        return ofNullable(findUserByIdentifierOrThrows(identifier).getArtist())
                .map(DomainEntity::toDTO);
    }

    @Override
    @Transactional
    public void follow(Identifier follower, Identifier following) {
        UserEntity followerUser = findUserByIdentifierOrThrows(follower);
        UserEntity followingUser = findUserByIdentifierOrThrows(following);

        followerUser.follow(followingUser);
        userRepository.save(followerUser);
        userRepository.save(followingUser);
    }

    @Override
    @Transactional
    public Set<UserDTO> retrieveFollowers(Identifier identifier) {
        UserEntity user = findUserByIdentifierOrThrows(identifier);

        return user.getFollowers().stream()
                .map(DomainEntity::toDTO)
                .collect(toSet());
    }

    @Override
    @Transactional
    public Set<UserDTO> retrieveFollowing(Identifier identifier) {
        UserEntity user = findUserByIdentifierOrThrows(identifier);

        return user.getFollowing().stream()
                .map(DomainEntity::toDTO)
                .collect(toSet());
    }

    @Override
    @Transactional
    public void unfollow(Identifier follower, Identifier following) {
        UserEntity followerUser = findUserByIdentifierOrThrows(follower);
        UserEntity followingUser = findUserByIdentifierOrThrows(following);

        followerUser.unfollow(followingUser);
        userRepository.save(followerUser);
        userRepository.save(followingUser);
    }

    @Override
    @Transactional
    public List<LikeDTO> retrieveLikes(Identifier identifier) {
        return findUserByIdentifierOrThrows(identifier)
                .getLikes()
                .stream()
                .map(DomainEntity::toDTO)
                .toList();
    }

    @Override
    @Transactional
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
