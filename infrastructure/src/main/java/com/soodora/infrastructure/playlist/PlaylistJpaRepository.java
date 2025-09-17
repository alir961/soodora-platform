package com.soodora.infrastructure.playlist;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PlaylistJpaRepository
        extends
            JpaRepository<PlaylistEntity, UUID>,
            JpaSpecificationExecutor<PlaylistEntity> {

    Optional<PlaylistEntity> findBySlug(String slug);

}
