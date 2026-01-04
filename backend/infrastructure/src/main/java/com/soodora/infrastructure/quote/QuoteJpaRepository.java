package com.soodora.infrastructure.quote;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteJpaRepository extends JpaRepository<QuoteEntity, UUID>, JpaSpecificationExecutor<QuoteEntity> {

    Optional<QuoteEntity> findBySlug(String slug);

    Optional<List<QuoteEntity>> findQuotesByArtistUserSlug(String slug);

}
