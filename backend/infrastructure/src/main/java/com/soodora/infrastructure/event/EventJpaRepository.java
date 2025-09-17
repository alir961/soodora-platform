package com.soodora.infrastructure.event;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventJpaRepository extends JpaRepository<EventEntity, UUID> {

    Optional<EventEntity> findBySlug(String slug);

}
