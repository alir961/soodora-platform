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

    private final UserJpaRepository repository;

    @Override
    public UserDTO save(RegisterCommand command) {
        UserEntity entity = new UserEntity();

        entity.setSlug(generateUniqueSlug());
        entity.setUsername(command.username());
        entity.setEmail(command.email().value());
        entity.setPassword(command.password());
        entity.setFirstname(command.firstname());
        entity.setLastname(command.lastname());
        return repository.save(entity).toDTO();
    }

    @Override
    public UserDTO find(Identifier identifier) {
        return findUserByIdentifier(identifier).toDTO();
    }

    @Override
    public List<UserDTO> findAll() {
        return repository.findAll().stream()
            .map(UserEntity::toDTO)
            .toList();
    }

    @Override
    public void delete(Identifier identifier) {
        UserEntity user = findUserByIdentifier(identifier);

        repository.deleteById(user.getId());
    }

    @Override
    public boolean isUsed(Identifier identifier) {
        try {
            findUserByIdentifier(identifier);
            return true;
        } catch (UnknownIdentifierException exception) {
            return false;
        }
    }

    @Override
    public String retrieveEncodedPassword(Identifier identifier) {
        return findUserByIdentifier(identifier)
            .getPassword();
    }

    @Override
    public Optional<ArtistDTO> retrieveArtist(Identifier identifier) {
        return ofNullable(findUserByIdentifier(identifier).getArtist())
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
        return findUserByIdentifier(identifier).getPlaylists().stream()
            .map(DomainEntity::toDTO)
            .toList();
    }

    private UserEntity findUserByIdentifier(Identifier identifier) throws UnknownIdentifierException {
        String value = identifier.value();
        Optional<UserEntity> findEntity = switch (identifier.type()) {
            case EMAIL -> repository.findByEmail(value);
            case SLUG -> repository.findBySlug(value);
        };

        return findEntity.orElseThrow(() -> new UnknownIdentifierException("Unable to find user '" + value + "'"));
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (repository.findBySlug(slug).isPresent())
            slug = generateSlug();
        return slug;
    }

}
