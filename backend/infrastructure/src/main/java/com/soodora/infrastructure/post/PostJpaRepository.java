package com.soodora.infrastructure.post;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostJpaRepository extends JpaRepository<PostEntity, UUID>, JpaSpecificationExecutor<PostEntity> {

    Optional<PostEntity> findBySlug(String slug);

    Page<PostEntity> findByArtistUserSlug(@Param("slug") String slug, Pageable pageable);

    Page<PostEntity> findByArtistUserSlugIn(List<String> userSlugs, Pageable pageable);

    Page<PostEntity> findByRepostsSlug(String slug, Pageable pageable);

    @Query("SELECT p FROM posts p WHERE p.artist.user.slug = :slug AND p.audioUrl IS NOT NULL AND p.audioUrl <> ''")
    Page<PostEntity> findByArtistSlugOrUserSlugAndAudioUrlIsNotNull(@Param("slug") String slug, Pageable pageable);

}
