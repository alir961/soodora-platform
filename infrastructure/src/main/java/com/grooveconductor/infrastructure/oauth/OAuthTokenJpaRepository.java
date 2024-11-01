package com.grooveconductor.infrastructure.oauth;

import com.grooveconductor.domain.oauth.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OAuthTokenJpaRepository extends JpaRepository<OAuthTokenEntity, UUID> {

    Optional<OAuthTokenEntity> findByProviderAndUserId(OAuthProvider provider, UUID userId);
    List<OAuthTokenEntity> findByUserId(UUID userId);

}
