package com.grooveconductor.internal.repository;

import com.grooveconductor.internal.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, UUID> {

    Optional<ProductEntity> findBySlug(String slug);

    Optional<ProductEntity> findByName(String name);

    Optional<List<ProductEntity>> findProductsByUserSlug(String slug);

}
