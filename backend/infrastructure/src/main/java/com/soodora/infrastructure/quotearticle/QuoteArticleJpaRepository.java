package com.soodora.infrastructure.quotearticle;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteArticleJpaRepository
        extends
            JpaRepository<QuoteArticleEntity, UUID>,
            JpaSpecificationExecutor<QuoteArticleEntity> {

    Optional<QuoteArticleEntity> findBySlug(String slug);

}
