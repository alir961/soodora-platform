package com.grooveconductor.application.oauth;

import com.grooveconductor.application.google.GoogleService;
import com.grooveconductor.domain.oauth.OAuthProvider;
import com.grooveconductor.domain.oauth.OAuthTokenRepository;
import com.grooveconductor.domain.oauth.RefreshableAuthorization;
import com.grooveconductor.domain.user.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuthTokenService {

    private final OAuthTokenRepository repository;
    private final GoogleService googleService;

    private final Map<String, String> userAccessTokens = new HashMap<>();

    public void updateUserAuthorization(OAuthProvider provider, UserDTO user, RefreshableAuthorization authorization) {
        if (repository.hasAuthorization(provider, user))
            repository.deleteAuthorization(provider, user);
        userAccessTokens.put(user.slug(), authorization.accessToken());
        repository.createAuthorization(provider, user, authorization.refreshToken());
    }

    public String retrieveUserAccessToken(OAuthProvider provider, UserDTO user) {
        if (userAccessTokens.containsKey(user.slug()))
            return userAccessTokens.get(user.slug());
        RefreshableAuthorization authorization = googleService.callRefreshTokenAPI(retrieveUserRefreshToken(provider, user));

        userAccessTokens.put(user.slug(), authorization.accessToken());
        return authorization.accessToken();
    }

    public String retrieveUserRefreshToken(OAuthProvider provider, UserDTO user) {
        if (repository.hasAuthorization(provider, user))
            return repository.retrieveAuthorization(provider, user);
        throw new OAuthFlowException("Unable to retrieve user #" + user.slug() + " refresh token for " + provider);
    }

}
