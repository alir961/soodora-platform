package com.grooveconductor.infrastructure.artist;

import com.grooveconductor.domain.artist.ArtistDTO;
import com.grooveconductor.domain.artist.ArtistRepository;
import com.grooveconductor.domain.artist.CreateArtistCommand;
import com.grooveconductor.domain.common.UnknownSlugException;
import com.grooveconductor.domain.event.EventDTO;
import com.grooveconductor.domain.user.UnknownEmailException;
import com.grooveconductor.domain.user.UnknownIdentifierException;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.infrastructure.common.DomainEntity;
import com.grooveconductor.infrastructure.user.UserEntity;
import com.grooveconductor.infrastructure.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.grooveconductor.infrastructure.common.SlugGenerator.generateSlug;

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
    public List<EventDTO> retrieveEvents(String slug) {
        ArtistEntity entity = findArtistBySlug(slug);

        return entity.getEvents().stream()
                .map(DomainEntity::toDTO)
                .toList();
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (repository.findBySlug(slug).isPresent())
            slug = generateSlug();
        return slug;
    }

    private ArtistEntity findArtistBySlug(String slug) throws UnknownIdentifierException {
        return repository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find artist '" + slug + "'"));
    }

}
