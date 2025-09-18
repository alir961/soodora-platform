package com.soodora.infrastructure.mediakititem;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaKitItemJpaRepository extends JpaRepository<MediaKitItemEntity, UUID> {

    Optional<MediaKitItemEntity> findBySlug(String slug);

    Optional<MediaKitItemEntity> findByMediaKitSlug(String mediaKitSlug);

    Optional<MediaKitItemEntity> findByMediaKitArtistUserSlug(String artistSlug);

}
