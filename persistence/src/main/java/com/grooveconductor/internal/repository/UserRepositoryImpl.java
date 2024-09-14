package com.grooveconductor.internal.repository;

import com.grooveconductor.domain.command.RegisterCommand;
import com.grooveconductor.domain.dto.UserDTO;
import com.grooveconductor.domain.exception.UnknownSlugException;
import com.grooveconductor.domain.exception.UnknownEmailException;
import com.grooveconductor.domain.repository.UserRepository;
import com.grooveconductor.internal.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.grooveconductor.internal.utils.SlugGenerator.generateSlug;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository repository;

    protected UserEntity findUserBySlugOrThrow(String slug) {
        return repository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find user with slug #" + slug));
    }

    @Override
    public UserDTO save(RegisterCommand command) {
        UserEntity entity = new UserEntity();

        entity.setSlug(generateUniqueSlug());
        entity.setUsername(command.username());
        entity.setEmail(command.email());
        entity.setPassword(command.password());
        entity.setFirstname(command.firstname());
        entity.setLastname(command.lastname());
        return repository.save(entity).toDTO();
    }

    @Override
    public UserDTO findBySlug(String slug) {
        return findUserBySlugOrThrow(slug).toDTO();
    }

    @Override
    public UserDTO findByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UnknownEmailException("Unable to find user '" + email + "'"))
                .toDTO();
    }

    @Override
    public List<UserDTO> findAll() {
        return repository.findAll().stream()
                .map(UserEntity::toDTO)
                .toList();
    }

    @Override
    public void deleteBySlug(String slug) {
        UserEntity user = findUserBySlugOrThrow(slug);

        repository.deleteById(user.getId());
    }

    @Override
    public boolean isSlugUsed(String slug) {
        return repository.findBySlug(slug).isPresent();
    }

    @Override
    public boolean isEmailUsed(String email) {
        return repository.findByEmail(email).isPresent();
    }

    @Override
    public String retrieveEncodedPassword(String email) {
        UserEntity user = repository.findByEmail(email)
                .orElseThrow(() -> new UnknownEmailException("Unable to find user '" + email + "'"));

        return user.getPassword();
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (repository.findBySlug(slug).isPresent())
            slug = generateSlug();
        return slug;
    }

}
