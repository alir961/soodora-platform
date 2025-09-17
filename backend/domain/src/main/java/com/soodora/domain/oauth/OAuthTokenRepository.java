package com.soodora.domain.oauth;

import com.soodora.domain.user.UserDTO;

import java.util.List;

public interface OAuthTokenRepository {

    void createAuthorization(OAuthProvider provider, UserDTO user, String externalId, String refreshToken);
    boolean hasAuthorization(OAuthProvider provider, UserDTO user);
    List<OAuthProvider> retrieveProviders(UserDTO user);

    String retrieveRefreshToken(OAuthProvider provider, UserDTO user);

    UserDTO retrieveUser(OAuthProvider provider, String externalId);
    void deleteAuthorization(OAuthProvider provider, UserDTO user);

}
