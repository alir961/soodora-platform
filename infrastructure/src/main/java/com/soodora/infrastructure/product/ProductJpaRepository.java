package com.soodora.infrastructure.product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductJpaRepository
        extends
            JpaRepository<ProductEntity, UUID>,
            JpaSpecificationExecutor<ProductEntity> {

    Optional<ProductEntity> findBySlug(String slug);

    Optional<ProductEntity> findByName(String name);

    Optional<List<ProductEntity>> findProductsByArtistSlug(String slug);

    @Query("SELECT p FROM products p WHERE p.artist.slug = :slug OR p.artist.user.slug = :slug")
    Page<ProductEntity> findByArtistSlugOrUserSlug(@Param("slug") String slug, Pageable pageable);

}
