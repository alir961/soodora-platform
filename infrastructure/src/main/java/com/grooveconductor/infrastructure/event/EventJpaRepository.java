package com.grooveconductor.infrastructure.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventJpaRepository extends JpaRepository<EventEntity, UUID> {

    Optional<EventEntity> findBySlug(String slug);

}
