package com.grooveconductor.infrastructure.product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, UUID> {

    Optional<ProductEntity> findBySlug(String slug);

    Optional<ProductEntity> findByName(String name);

    Optional<List<ProductEntity>> findProductsByUserSlug(String slug);

}
