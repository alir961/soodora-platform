package com.grooveconductor.infrastructure.artist;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistJpaRepository extends JpaRepository<ArtistEntity, UUID> {

    Optional<ArtistEntity> findBySlug(String slug);

}
