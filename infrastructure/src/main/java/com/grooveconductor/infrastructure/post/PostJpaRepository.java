package com.grooveconductor.infrastructure.post;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostJpaRepository extends JpaRepository<PostEntity, UUID> {

  Optional<PostEntity> findBySlug(String slug);

  Optional<List<PostEntity>> findPostByUserSlug(String slug);

}
