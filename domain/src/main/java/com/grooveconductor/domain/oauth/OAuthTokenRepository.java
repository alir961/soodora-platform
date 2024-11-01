package com.grooveconductor.domain.oauth;

import com.grooveconductor.domain.user.UserDTO;

import java.util.List;

public interface OAuthTokenRepository {

    void createAuthorization(OAuthProvider provider, UserDTO user, String refreshToken);
    boolean hasAuthorization(OAuthProvider provider, UserDTO user);
    List<OAuthProvider> retrieveProviders(UserDTO user);
    String retrieveAuthorization(OAuthProvider provider, UserDTO user);
    void deleteAuthorization(OAuthProvider provider, UserDTO user);

}
