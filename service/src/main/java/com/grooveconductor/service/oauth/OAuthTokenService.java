package com.grooveconductor.service.oauth;

import com.grooveconductor.domain.auth.TokenRepository;
import com.grooveconductor.domain.oauth.OAuthProvider;
import com.grooveconductor.domain.oauth.OAuthTokenRepository;
import com.grooveconductor.domain.oauth.RefreshableAuthorization;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.domain.user.UserRepository;
import com.grooveconductor.service.auth.AuthenticationException;
import com.grooveconductor.service.google.GoogleFacade;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import static com.grooveconductor.domain.user.IdentifierFactory.createFromEmail;

@Service
@RequiredArgsConstructor
public class OAuthTokenService {

    private final OAuthTokenRepository oauthRepository;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    private final GoogleFacade googleFacade;

    private final Map<String, String> userAccessTokens = new HashMap<>();

    public void updateUserAuthorization(OAuthProvider provider, UserDTO user, RefreshableAuthorization authorization) {
        if (oauthRepository.hasAuthorization(provider, user))
            oauthRepository.deleteAuthorization(provider, user);
        userAccessTokens.put(user.slug(), authorization.accessToken());
        oauthRepository.createAuthorization(provider, user, authorization.refreshToken());
    }

    public URI retrieveUserOAuthURI(String token) {
        retrieveUserOrThrows(token);
        return googleFacade.getOAuthAuthorizationFlowURI(token);
    }

    public String retrieveUserAccessToken(OAuthProvider provider, UserDTO user) {
        if (userAccessTokens.containsKey(user.slug()))
            return userAccessTokens.get(user.slug());
        String refreshToken = retrieveUserRefreshToken(provider, user);
        RefreshableAuthorization authorization = googleFacade.refreshOAuthToken(refreshToken);

        userAccessTokens.put(user.slug(), authorization.accessToken());
        return authorization.accessToken();
    }

    public String retrieveUserRefreshToken(OAuthProvider provider, UserDTO user) {
        if (oauthRepository.hasAuthorization(provider, user))
            return oauthRepository.retrieveAuthorization(provider, user);
        throw new OAuthFlowException("Unable to retrieve user #" + user.slug() + " refresh token for " + provider);
    }

    public UserDTO retrieveUserOrThrows(String token) throws AuthenticationException {
        if (token == null || token.isEmpty())
            throw new AuthenticationException("Token is null or empty");
        String email = tokenRepository.getEmailFromToken(token);
        UserDTO user = userRepository.find(createFromEmail(email));

        if (email == null || user == null)
            throw new AuthenticationException("Unable to retrieve user");
        return user;
    }

    public void deleteUserAuthorization(OAuthProvider provider, UserDTO user) {
        oauthRepository.deleteAuthorization(provider, user);
    }

}
