package com.soodora.infrastructure.product;

import com.soodora.domain.product.ProductStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Check and update in one statement, so two buyers cannot both take the last
     * item.
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE products p SET p.quantity = p.quantity - :quantity "
            + "WHERE p.slug = :slug AND p.quantity >= :quantity")
    int decreaseQuantity(@Param("slug") String slug, @Param("quantity") long quantity);

}
