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

        entity.setUser(userEntity);

        return repository.save(entity)
                .toDTO();
    }

    @Override
    @Transactional
    public ArtistDTO find(String slug) {
        return findArtistByUserSlug(slug)
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
        ArtistEntity entity = findArtistByUserSlug(slug);

        repository.delete(entity);
    }

    @Override
    @Transactional
    public List<EventDTO> retrieveEvents(String slug) {
        ArtistEntity entity = findArtistByUserSlug(slug);

        return entity.getEvents().stream()
                .map(DomainEntity::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public LinkPageDTO retrieveLinkPage(String slug) throws LinkPageNotFoundException {
        ArtistEntity artist = findArtistByUserSlug(slug);

        if (artist.getLinkPage() == null)
            throw new LinkPageNotFoundException("Link Page not found for artist " + slug);
        return artist.getLinkPage().toDTO();
    }

    private ArtistEntity findArtistByUserSlug(String slug) throws UnknownIdentifierException {
        return repository.findByUserSlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find artist '" + slug + "'"));
    }

}
