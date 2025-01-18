package com.grooveconductor.infrastructure.playlist;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlaylistJpaRepository extends JpaRepository<PlaylistEntity, UUID> {

    Optional<PlaylistEntity> findBySlug(String slug);

    Optional<List<PlaylistEntity>> findPlaylistsByUserSlug(String slug);

}
