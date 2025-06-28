package com.soodora.infrastructure.product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductJpaRepository
        extends
            JpaRepository<ProductEntity, UUID>,
            JpaSpecificationExecutor<ProductEntity> {

    Optional<ProductEntity> findBySlug(String slug);

    Optional<ProductEntity> findByName(String name);

    Optional<List<ProductEntity>> findProductsByUserSlug(String slug);

    Page<ProductEntity> findByUserSlug(String slug, Pageable pageable);

}
