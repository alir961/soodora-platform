package com.soodora.infrastructure.linkhub;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkHubJpaRepository extends JpaRepository<LinkHubEntity, UUID> {

    Optional<LinkHubEntity> findBySlug(String slug);

    Optional<LinkHubEntity> findByArtistUserSlug(String slug);

}
