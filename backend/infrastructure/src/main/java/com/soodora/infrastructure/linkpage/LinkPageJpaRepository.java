package com.soodora.infrastructure.linkpage;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkPageJpaRepository extends JpaRepository<LinkPageEntity, UUID> {

    Optional<LinkPageEntity> findBySlug(String slug);

    Optional<LinkPageEntity> findByUserSlug(String slug);

}
