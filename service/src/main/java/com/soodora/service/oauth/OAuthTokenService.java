package com.soodora.service.oauth;

import com.soodora.domain.auth.AuthenticationTokenException;
import com.soodora.domain.auth.TokenRepository;
import com.soodora.domain.oauth.*;
import com.soodora.domain.user.User;
import com.soodora.domain.user.UserRepository;
import com.soodora.service.auth.AuthenticationException;
import com.soodora.service.google.GoogleFacade;

import java.net.URI;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.soodora.domain.oauth.OAuthProvider.GOOGLE;
import static com.soodora.domain.user.value.Identifier.Factory.createFromEmail;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthTokenService {

    @Value("${application.url:localhost}")
    private String frontUrl;

    private final OAuthTokenRepository oauthRepository;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    private final AuthorizationMapper authorizationMapper;

    private final GoogleFacade googleFacade;

    private final List<String> loginStates = new ArrayList<>();
    private final Map<String, String> userAccessTokens = new HashMap<>();

    public String processGoogleAuthentication(String code, String state) {
        RefreshableAuthorization authorization = googleFacade.exchangeOAuthToken(code);
        String externalId = tokenRepository.getSubjectFromGoogleToken(authorization.externalToken());
        User user = processLogin(authorization, state);
        String baseUrl = frontUrl + "/authentication";

        if (user != null)
            return baseUrl + "?token=" + tokenRepository.generateToken(user);
        try {
            if (loginStates.contains(state)) {
                String key = authorizationMapper.registerAuthorization(authorization);

                loginStates.remove(state);
                return baseUrl + "?external=" + key;
            }
            user = retrieveUserOrThrows(state);

            try {
                updateGoogleUserAuthorization(user, externalId, authorization);
            } catch (HttpClientErrorException | AuthenticationTokenException ignored) {
                deleteUserAuthorization(GOOGLE, user);
                return baseUrl + "?error=oauth";
            }
        } catch (AuthenticationException | OAuthAuthorizationException ignored) {
            return baseUrl + "?error=oauth";
        }
        return baseUrl;
    }

    public URI retrieveGogoleUnauthorizedOAuthURI() {
        Random random = new Random();
        String state = String.valueOf(random.nextLong());

        while (loginStates.contains(state))
            state = String.valueOf(random.nextLong());
        loginStates.add(state);
        return googleFacade.getOAuthAuthorizationFlowURI(state);
    }

    public URI retrieveUserGoogleOAuthURI(String token) {
        retrieveUserOrThrows(token);
        return googleFacade.getOAuthAuthorizationFlowURI(token);
    }

    public String retrieveUserAccessToken(OAuthProvider provider, User user) {
        if (userAccessTokens.containsKey(user.getSlug()))
            return userAccessTokens.get(user.getSlug());
        String refreshToken = retrieveUserRefreshToken(provider, user);
        RefreshableAuthorization authorization = googleFacade.refreshOAuthToken(refreshToken);

        userAccessTokens.put(user.getSlug(), authorization.accessToken());
        return authorization.accessToken();
    }

    public String retrieveUserRefreshToken(OAuthProvider provider, User user) {
        if (oauthRepository.hasAuthorization(provider, user))
            return oauthRepository.retrieveRefreshToken(provider, user);
        throw new OAuthFlowException(
                "Unable to retrieve user #" + user.getSlug() + " refresh externalToken for " + provider);
    }

    public User retrieveUserOrThrows(String token) throws AuthenticationException {
        if (token == null || token.isEmpty())
            throw new AuthenticationException("Token is null or empty");
        String email = tokenRepository.getSubjectFromToken(token);
        User user = userRepository.find(createFromEmail(email));

        if (email == null || user == null)
            throw new AuthenticationException("Unable to retrieve user");
        return user;
    }

    public void deleteUserAuthorization(OAuthProvider provider, User user) {
        oauthRepository.deleteAuthorization(provider, user);
    }

    private User processLogin(RefreshableAuthorization authorization, String state) {
        String externalId = tokenRepository.getSubjectFromGoogleToken(authorization.externalToken());

        if (loginStates.contains(state)) {
            try {
                User user = oauthRepository.retrieveUser(GOOGLE, externalId);

                loginStates.remove(state);
                return user;
            } catch (OAuthAuthorizationException ignored) {
                return null;
            }
        }
        return null;
    }

    private void updateGoogleUserAuthorization(User user, String externalId, RefreshableAuthorization authorization) {
        if (oauthRepository.hasAuthorization(GOOGLE, user))
            oauthRepository.deleteAuthorization(GOOGLE, user);
        userAccessTokens.put(user.getSlug(), authorization.accessToken());
        oauthRepository.createAuthorization(GOOGLE, user, externalId, authorization.refreshToken());
    }

}
