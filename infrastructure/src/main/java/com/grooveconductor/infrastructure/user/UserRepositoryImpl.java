package com.grooveconductor.infrastructure.user;

import com.grooveconductor.domain.auth.RegisterCommand;
import com.grooveconductor.domain.artist.ArtistDTO;
import com.grooveconductor.domain.playlist.PlaylistDTO;
import com.grooveconductor.domain.product.ProductDTO;
import com.grooveconductor.domain.user.Identifier;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.domain.user.UnknownIdentifierException;
import com.grooveconductor.domain.user.UserRepository;
import com.grooveconductor.infrastructure.common.DomainEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.grooveconductor.infrastructure.common.SlugGenerator.generateSlug;
import static java.util.Optional.ofNullable;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userRepository;

    @Override
    public UserDTO save(RegisterCommand command) {
        UserEntity user = new UserEntity();

        user.setSlug(generateUniqueSlug());
        user.setUsername(command.username());
        user.setEmail(command.email().value());
        user.setPassword(command.password());
        user.setFirstname(command.firstname());
        user.setLastname(command.lastname());
        return userRepository.save(user).toDTO();
    }

    @Override
    public UserDTO find(Identifier identifier) {
        return findUserByIdentifierOrThrows(identifier).toDTO();
    }

    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
            .map(UserEntity::toDTO)
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
        return findUserByIdentifier(identifier).getProducts().stream()
            .map(DomainEntity::toDTO)
            .toList();
    }

    @Override
    public List<PlaylistDTO> retrievePlaylists(Identifier identifier) {
        return findUserByIdentifierOrThrows(identifier).getPlaylists().stream()
            .map(DomainEntity::toDTO)
            .toList();
    }

    private UserEntity findUserByIdentifierOrThrows(Identifier identifier) throws UnknownIdentifierException {
        String value = identifier.value();
        Optional<UserEntity> findEntity = switch (identifier.type()) {
            case EMAIL -> userRepository.findByEmail(value);
            case SLUG -> userRepository.findBySlug(value);
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
