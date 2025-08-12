package com.soodora.infrastructure.artist;

import com.soodora.domain.artist.ArtistDTO;
import com.soodora.domain.artist.ArtistRepository;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.event.EventDTO;
import com.soodora.domain.linkpage.LinkPageDTO;
import com.soodora.domain.user.*;
import com.soodora.infrastructure.auth.RoleEntity;
import com.soodora.infrastructure.auth.RoleJpaRepository;
import com.soodora.infrastructure.common.DomainEntity;
import com.soodora.infrastructure.user.UserEntity;
import com.soodora.infrastructure.user.UserJpaRepository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import static com.soodora.infrastructure.common.SlugGenerator.generateSlug;

@Repository
@RequiredArgsConstructor
public class ArtistRepositoryImpl implements ArtistRepository {

    private final ArtistJpaRepository repository;
    private final UserJpaRepository userRepository;
    private final RoleJpaRepository roleRepository;

    @Override
    @Transactional
    public ArtistDTO save(UserDTO user) {
        ArtistEntity entity = new ArtistEntity();
        RoleEntity artistRole = roleRepository.findByName("ROLE_ARTIST");
        UserEntity userEntity = userRepository.findByEmail(user.email())
                .orElseThrow(() -> new UnknownEmailException("Unable to find user '" + user.email() + "'"));
        Set<RoleEntity> userRoles = userEntity.getRoles();

        userRoles.add(artistRole);
        userEntity.setRoles(userRoles);
        userEntity = userRepository.save(userEntity);

        entity.setName(userEntity.getUsername());
        entity.setDescription(userEntity.getDescription());
        entity.setImageUrl(userEntity.getAvatarUrl());
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
