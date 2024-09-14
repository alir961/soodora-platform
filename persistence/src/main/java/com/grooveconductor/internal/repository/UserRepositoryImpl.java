package com.grooveconductor.internal.repository;

import com.grooveconductor.domain.command.RegisterCommand;
import com.grooveconductor.domain.dto.Identifier;
import com.grooveconductor.domain.dto.UserDTO;
import com.grooveconductor.domain.exception.UnknownIdentifierException;
import com.grooveconductor.domain.repository.UserRepository;
import com.grooveconductor.internal.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.grooveconductor.internal.utils.SlugGenerator.generateSlug;

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
