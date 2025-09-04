package com.soodora.infrastructure.mediakit;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaKitJpaRepository extends JpaRepository<MediaKitEntity, UUID> {

    Optional<MediaKitEntity> findBySlug(String slug);

    Optional<MediaKitEntity> findByArtistUserSlug(String artistSlug);

}
