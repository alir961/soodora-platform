package com.soodora.infrastructure.sale;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SaleJpaRepository extends JpaRepository<SaleEntity, UUID> {

    Optional<SaleEntity> findBySlug(String slug);

    @Query("SELECT s FROM sales s WHERE s.artist.slug = :slug OR s.artist.user.slug = :slug")
    Page<SaleEntity> findByArtistSlugOrArtistUserSlug(@Param("slug") String slug, Pageable pageable);

}
