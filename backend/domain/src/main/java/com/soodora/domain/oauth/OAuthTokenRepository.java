package com.soodora.domain.oauth;

import com.soodora.domain.user.User;

import java.util.List;

public interface OAuthTokenRepository {

    void createAuthorization(OAuthProvider provider, User user, String externalId, String refreshToken);
    boolean hasAuthorization(OAuthProvider provider, User user);
    List<OAuthProvider> retrieveProviders(User user);
    String retrieveRefreshToken(OAuthProvider provider, User user);
    User retrieveUser(OAuthProvider provider, String externalId);
    void deleteAuthorization(OAuthProvider provider, User user);

}
