package com.soodora.service.oauth;

import com.soodora.domain.auth.AuthenticationTokenException;
import com.soodora.domain.auth.TokenRepository;
import com.soodora.domain.oauth.*;
import com.soodora.domain.user.UserDTO;
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
import static com.soodora.domain.user.IdentifierFactory.createFromEmail;

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
        UserDTO user = processLogin(authorization, state);
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
            return oauthRepository.retrieveRefreshToken(provider, user);
        throw new OAuthFlowException(
                "Unable to retrieve user #" + user.slug() + " refresh externalToken for " + provider);
    }

    public UserDTO retrieveUserOrThrows(String token) throws AuthenticationException {
        if (token == null || token.isEmpty())
            throw new AuthenticationException("Token is null or empty");
        String email = tokenRepository.getSubjectFromToken(token);
        UserDTO user = userRepository.find(createFromEmail(email));

        if (email == null || user == null)
            throw new AuthenticationException("Unable to retrieve user");
        return user;
    }

    public void deleteUserAuthorization(OAuthProvider provider, UserDTO user) {
        oauthRepository.deleteAuthorization(provider, user);
    }

    private UserDTO processLogin(RefreshableAuthorization authorization, String state) {
        String externalId = tokenRepository.getSubjectFromGoogleToken(authorization.externalToken());

        if (loginStates.contains(state)) {
            try {
                UserDTO user = oauthRepository.retrieveUser(GOOGLE, externalId);

                loginStates.remove(state);
                return user;
            } catch (OAuthAuthorizationException ignored) {
                return null;
            }
        }
        return null;
    }

    private void updateGoogleUserAuthorization(UserDTO user, String externalId,
            RefreshableAuthorization authorization) {
        if (oauthRepository.hasAuthorization(GOOGLE, user))
            oauthRepository.deleteAuthorization(GOOGLE, user);
        userAccessTokens.put(user.slug(), authorization.accessToken());
        oauthRepository.createAuthorization(GOOGLE, user, externalId, authorization.refreshToken());
    }

}
