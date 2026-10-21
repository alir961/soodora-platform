package com.soodora.infrastructure.sale;

import com.soodora.infrastructure.product.ProductEntity;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleJpaRepository extends JpaRepository<SaleEntity, UUID> {

    Optional<SaleEntity> findBySlug(String slug);

    Page<SaleEntity> findByVendorUserSlug(String slug, Pageable pageable);

    boolean existsByProduct(ProductEntity product);

    boolean existsByPaymentReference(String paymentReference);

}
