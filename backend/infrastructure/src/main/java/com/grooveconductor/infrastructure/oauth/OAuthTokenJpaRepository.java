package com.grooveconductor.infrastructure.oauth;

import com.grooveconductor.domain.oauth.OAuthProvider;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuthTokenJpaRepository extends JpaRepository<OAuthTokenEntity, UUID> {

    Optional<OAuthTokenEntity> findByProviderAndUserId(OAuthProvider provider, UUID userId);

    Optional<OAuthTokenEntity> findByProviderAndExternalId(OAuthProvider provider, String externalId);
    List<OAuthTokenEntity> findByUserId(UUID userId);

}
