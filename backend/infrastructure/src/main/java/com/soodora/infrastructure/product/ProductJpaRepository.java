package com.soodora.infrastructure.product;

import com.soodora.domain.product.ProductStatus;

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

    Page<ProductEntity> findByStatus(ProductStatus status, Pageable pageable);

    Optional<List<ProductEntity>> findProductsByVendorUserSlug(String slug);

    Page<ProductEntity> findByVendorUserSlug(String slug, Pageable pageable);

}
