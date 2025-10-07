package com.soodora.infrastructure.oauth;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.oauth.OAuthAuthorizationException;
import com.soodora.domain.oauth.OAuthProvider;
import com.soodora.domain.oauth.OAuthTokenRepository;
import com.soodora.domain.user.User;
import com.soodora.infrastructure.common.DomainEntityAdapter;
import com.soodora.infrastructure.user.UserEntity;
import com.soodora.infrastructure.user.UserJpaRepository;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OAuthTokenRepositoryImpl implements OAuthTokenRepository {

    private final OAuthTokenJpaRepository oauthTokenRepository;
    private final UserJpaRepository userRepository;

    @Override
    @Transactional
    public void createAuthorization(OAuthProvider provider, User user, String externalId, String refreshToken) {
        if (hasAuthorization(provider, user))
            throw new OAuthAuthorizationException("User has already an authorization set for this provider");
        OAuthTokenEntity entity = new OAuthTokenEntity();

        entity.setUser(fetchEntity(user));
        entity.setProvider(provider);
        entity.setExternalId(externalId);
        entity.setRefreshToken(refreshToken);
        oauthTokenRepository.save(entity);
    }

    @Override
    @Transactional
    public boolean hasAuthorization(OAuthProvider provider, User user) {
        return oauthTokenRepository.findByProviderAndUserId(provider, fetchEntity(user).getId())
                .isPresent();
    }

    @Override
    @Transactional
    public List<OAuthProvider> retrieveProviders(User user) {
        return oauthTokenRepository.findByUserId(fetchEntity(user).getId()).stream()
                .map(OAuthTokenEntity::getProvider)
                .toList();
    }

    @Override
    @Transactional
    public String retrieveRefreshToken(OAuthProvider provider, User user) {
        String exceptionMessage = "Unable to find " + provider + "authorization for user #" + user.getSlug();

        return oauthTokenRepository.findByProviderAndUserId(provider, fetchEntity(user).getId())
                .map(OAuthTokenEntity::getRefreshToken)
                .orElseThrow(() -> new OAuthAuthorizationException(exceptionMessage));
    }

    @Override
    @Transactional
    public User retrieveUser(OAuthProvider provider, String externalId) {
        String exceptionMessage = "Unable to find " + provider + " authorization for external id #" + externalId;

        return oauthTokenRepository.findByProviderAndExternalId(provider, externalId)
                .map(OAuthTokenEntity::getUser)
                .map(DomainEntityAdapter::aggregate)
                .orElseThrow(() -> new OAuthAuthorizationException(exceptionMessage));
    }

    @Override
    @Transactional
    public void deleteAuthorization(OAuthProvider provider, User user) {
        String exceptionMessage = "Unable to find " + provider + "authorization for user #" + user.getSlug();
        OAuthTokenEntity entity = oauthTokenRepository.findByProviderAndUserId(provider, fetchEntity(user).getId())
                .orElseThrow(() -> new OAuthAuthorizationException(exceptionMessage));

        oauthTokenRepository.delete(entity);
    }

    private UserEntity fetchEntity(User user) throws UnknownSlugException {
        return userRepository.findBySlug(user.getSlug())
                .orElseThrow(() -> new UnknownSlugException("Unable to find user #" + user.getSlug()));
    }

}
