package com.grooveconductor.infrastructure.oauth;

import com.grooveconductor.domain.common.UnknownSlugException;
import com.grooveconductor.domain.oauth.OAuthAuthorizationException;
import com.grooveconductor.domain.oauth.OAuthProvider;
import com.grooveconductor.domain.oauth.OAuthTokenRepository;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.infrastructure.user.UserEntity;
import com.grooveconductor.infrastructure.user.UserJpaRepository;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OAuthTokenRepositoryImpl implements OAuthTokenRepository {

    private final OAuthTokenJpaRepository repository;
    private final UserJpaRepository userRepository;

    @Override
    @Transactional
    public void createAuthorization(OAuthProvider provider, UserDTO user, String externalId, String refreshToken) {
        if (hasAuthorization(provider, user))
            throw new OAuthAuthorizationException("User has already an authorization set for this provider");
        OAuthTokenEntity entity = new OAuthTokenEntity();

        entity.setUser(userFromDTO(user));
        entity.setProvider(provider);
        entity.setExternalId(externalId);
        entity.setRefreshToken(refreshToken);
        repository.save(entity);
    }

    @Override
    @Transactional
    public boolean hasAuthorization(OAuthProvider provider, UserDTO user) {
        return repository.findByProviderAndUserId(provider, userFromDTO(user).getId())
                .isPresent();
    }

    @Override
    @Transactional
    public List<OAuthProvider> retrieveProviders(UserDTO user) {
        return repository.findByUserId(userFromDTO(user).getId()).stream()
                .map(OAuthTokenEntity::getProvider)
                .toList();
    }

    @Override
    @Transactional
    public String retrieveRefreshToken(OAuthProvider provider, UserDTO user) {
        String exceptionMessage = "Unable to find " + provider + "authorization for user #" + user.slug();

        return repository.findByProviderAndUserId(provider, userFromDTO(user).getId())
                .map(OAuthTokenEntity::getRefreshToken)
                .orElseThrow(() -> new OAuthAuthorizationException(exceptionMessage));
    }

    @Override
    @Transactional
    public UserDTO retrieveUser(OAuthProvider provider, String externalId) {
        String exceptionMessage = "Unable to find " + provider + " authorization for external id #" + externalId;

        return repository.findByProviderAndExternalId(provider, externalId)
                .map(OAuthTokenEntity::getUser)
                .map(UserEntity::toDTO)
                .orElseThrow(() -> new OAuthAuthorizationException(exceptionMessage));
    }

    @Override
    @Transactional
    public void deleteAuthorization(OAuthProvider provider, UserDTO user) {
        String exceptionMessage = "Unable to find " + provider + "authorization for user #" + user.slug();
        OAuthTokenEntity entity = repository.findByProviderAndUserId(provider, userFromDTO(user).getId())
                .orElseThrow(() -> new OAuthAuthorizationException(exceptionMessage));

        repository.delete(entity);
    }

    private UserEntity userFromDTO(UserDTO user) throws UnknownSlugException {
        return userRepository.findBySlug(user.slug())
                .orElseThrow(() -> new UnknownSlugException("Unable to find user #" + user.slug()));
    }

}
