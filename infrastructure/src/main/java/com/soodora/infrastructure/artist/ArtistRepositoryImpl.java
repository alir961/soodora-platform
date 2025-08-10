package com.soodora.infrastructure.artist;

import com.soodora.domain.artist.ArtistDTO;
import com.soodora.domain.artist.ArtistRepository;
import com.soodora.domain.artist.CreateArtistCommand;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.event.EventDTO;
import com.soodora.domain.linkpage.LinkPageDTO;
import com.soodora.domain.user.*;
import com.soodora.infrastructure.common.DomainEntity;
import com.soodora.infrastructure.user.UserEntity;
import com.soodora.infrastructure.user.UserJpaRepository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import static com.soodora.infrastructure.common.SlugGenerator.generateSlug;

@Repository
@RequiredArgsConstructor
public class ArtistRepositoryImpl implements ArtistRepository {

    private final ArtistJpaRepository repository;
    private final UserJpaRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public ArtistDTO save(CreateArtistCommand command, UserDTO user) {
        ArtistEntity entity = new ArtistEntity();
        UserEntity userEntity = userRepository.findByEmail(user.email())
                .orElseThrow(() -> new UnknownEmailException("Unable to find user '" + user.email() + "'"));
        jdbcTemplate.update("INSERT INTO users_roles (user_id, role_id) VALUES (?, ?)", userEntity.getId(), 1);

        entity.setName(command.name());
        entity.setDescription(command.description());
        entity.setImageUrl(command.imageUrl());
        entity.setSlug(generateUniqueSlug());
        entity.setUser(userEntity);
        return repository.save(entity)
                .toDTO();
    }

    @Override
    @Transactional
    public ArtistDTO find(String slug) {
        return findArtistBySlug(slug)
                .toDTO();
    }

    @Override
    @Transactional
    public List<ArtistDTO> findAll() {
        return repository.findAll().stream()
                .map(DomainEntity::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public void delete(String slug) {
        ArtistEntity entity = findArtistBySlug(slug);

        repository.delete(entity);
    }

    @Override
    @Transactional
    public List<EventDTO> retrieveEvents(String slug) {
        ArtistEntity entity = findArtistBySlug(slug);

        return entity.getEvents().stream()
                .map(DomainEntity::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public LinkPageDTO retrieveLinkPage(String slug) throws LinkPageNotFoundException {
        ArtistEntity artist = findArtistBySlug(slug);
        if (artist.getLinkPage() == null)
            throw new LinkPageNotFoundException("Link Page not found for artist " + slug);

        return artist.getLinkPage().toDTO();
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
