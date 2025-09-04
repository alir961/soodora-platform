package com.soodora.infrastructure.sale;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleJpaRepository extends JpaRepository<SaleEntity, UUID> {

    Optional<SaleEntity> findBySlug(String slug);

    Page<SaleEntity> findByArtistUserSlug(String slug, Pageable pageable);

}
