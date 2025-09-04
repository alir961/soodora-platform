package com.soodora.infrastructure.link;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkJpaRepository extends JpaRepository<LinkEntity, UUID> {

    Optional<LinkEntity> findBySlug(String slug);

}
