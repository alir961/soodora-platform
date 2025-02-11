package com.grooveconductor.infrastructure.playlist;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistJpaRepository extends JpaRepository<PlaylistEntity, UUID> {

    Optional<PlaylistEntity> findBySlug(String slug);

    Optional<List<PlaylistEntity>> findPlaylistsByUserSlug(String slug);

}
