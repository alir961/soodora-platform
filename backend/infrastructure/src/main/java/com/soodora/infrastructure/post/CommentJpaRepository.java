package com.soodora.infrastructure.post;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, UUID> {

    Optional<CommentEntity> findBySlug(String slug);

}
