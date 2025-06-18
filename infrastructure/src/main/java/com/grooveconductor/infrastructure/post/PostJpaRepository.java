package com.grooveconductor.infrastructure.post;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostJpaRepository extends JpaRepository<PostEntity, UUID> {

    Optional<PostEntity> findBySlug(String slug);

    Optional<List<PostEntity>> findPostByUserSlug(String slug);

    Page<PostEntity> findByUserSlugIn(List<String> userSlugs, Pageable pageable);

}
