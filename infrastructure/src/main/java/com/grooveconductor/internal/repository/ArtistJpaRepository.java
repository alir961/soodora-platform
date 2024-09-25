package com.grooveconductor.internal.repository;

import com.grooveconductor.internal.entity.ArtistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArtistJpaRepository extends JpaRepository<ArtistEntity, UUID> {

    Optional<ArtistEntity> findBySlug(String slug);

}
