package com.grooveconductor.internal.repository;

import com.grooveconductor.domain.command.CreateArtistCommand;
import com.grooveconductor.domain.dto.ArtistDTO;
import com.grooveconductor.domain.dto.UserDTO;
import com.grooveconductor.domain.exception.UnknownEmailException;
import com.grooveconductor.domain.exception.UnknownIdentifierException;
import com.grooveconductor.domain.repository.ArtistRepository;
import com.grooveconductor.domain.repository.UserRepository;
import com.grooveconductor.internal.entity.ArtistEntity;
import com.grooveconductor.internal.entity.DomainEntity;
import com.grooveconductor.internal.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.parser.Entity;
import java.util.List;

import static com.grooveconductor.internal.utils.SlugGenerator.generateSlug;

@Repository
@RequiredArgsConstructor
public class ArtistRepositoryImpl implements ArtistRepository {

    private final ArtistJpaRepository repository;
    private final UserJpaRepository userRepository;

    @Override
    public ArtistDTO save(CreateArtistCommand command, UserDTO user) {
        ArtistEntity entity = new ArtistEntity();
        UserEntity userEntity = userRepository.findByEmail(user.email())
                .orElseThrow(() -> new UnknownEmailException("Unable to find user '" + user.email() + "'"));

        entity.setName(command.name());
        entity.setDescription(command.description());
        entity.setImageUrl(command.imageUrl());
        entity.setSlug(generateUniqueSlug());
        entity.setUser(userEntity);
        return repository.save(entity)
                .toDTO();
    }

    @Override
    public ArtistDTO find(String slug) {
        return findArtistBySlug(slug)
                .toDTO();
    }

    @Override
    public List<ArtistDTO> findAll() {
        return repository.findAll().stream()
                .map(DomainEntity::toDTO)
                .toList();
    }

    @Override
    public void delete(String slug) {
        ArtistEntity entity = findArtistBySlug(slug);

        repository.delete(entity);
    }

    @Override
    public boolean isUsed(String slug) {
        return repository.findBySlug(slug)
                .isPresent();
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (repository.findBySlug(slug).isPresent())
            slug = generateSlug();
        return slug;
    }

    private ArtistEntity findArtistBySlug(String slug) throws UnknownIdentifierException {
        return repository.findBySlug(slug)
                .orElseThrow(() -> new UnknownIdentifierException("Unable to find artist '" + slug + "'"));
    }

}
